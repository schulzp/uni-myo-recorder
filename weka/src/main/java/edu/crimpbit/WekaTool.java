package edu.crimpbit;

import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WekaTool {

    @Autowired
    private GestureService gestureService;

    private static AverageFilter averageFilter;
    private static LabelFilter labelFilter;
    private static EnvelopeFollowerFilter envelopeFollowerFilter;

    public WekaTool() {
    }

    public WekaTool(Builder builder) {
        averageFilter = builder.averageFilter;
        labelFilter = builder.labelFilter;
        envelopeFollowerFilter = builder.envelopeFollowerFilter;
    }

    public Instances convertToTrainSet(List<Recording> recordings, List<Gesture> gestures) {
        return convert(recordings, gestures);
    }

    public List<Instances> convertToTestSet(Recording recording, List<Gesture> gestures) {
        List<Recording> recordings = new ArrayList<>();
        List<Instances> instances = new ArrayList<>();
        recordings.add(recording);
        instances.add(convert(recordings, gestures));
        for (Gesture gesture : gestures) {
            if (!gesture.getName().equals(recording.getGesture().getName())) {
                List<Recording> r = new ArrayList<>();
                Recording temp = recording;
                temp.setGesture(gesture);
                r.add(temp);
                instances.add(convert(r, gestures));
            }
        }
        return instances;
    }

    public Instances convert(List<Recording> recordings, List<Gesture> gestures) {
        FastVector attInfo = new FastVector();

        Attribute emg_0 = new Attribute("emg_0", 0);
        Attribute emg_1 = new Attribute("emg_1", 1);
        Attribute emg_2 = new Attribute("emg_2", 2);
        Attribute emg_3 = new Attribute("emg_3", 3);
        Attribute emg_4 = new Attribute("emg_4", 4);
        Attribute emg_5 = new Attribute("emg_5", 5);
        Attribute emg_6 = new Attribute("emg_6", 6);
        Attribute emg_7 = new Attribute("emg_7", 7);

        FastVector fastVector = new FastVector();


        for (Gesture gesture : gestures) {
            //fastVector.addElement(new Attribute(gesture.getName(), (FastVector) null));
            fastVector.addElement(gesture.getName());
        }

//        fastVector.addElement("index");
//        fastVector.addElement("index+middle");
//        fastVector.addElement("index+middle+ring");
//        fastVector.addElement("index+thumb");
//        fastVector.addElement("index+middle+thumb");
//        fastVector.addElement("index+middle+ring+thumb");

//
//
//        fastVector.addElement("index-finger");
//        fastVector.addElement("index-finger+middle-finger");
//        fastVector.addElement("index-finger+middle-finger+ring-finger");
//


        Attribute classnameAttribute = new Attribute("grip_type", fastVector, 8);
        //System.out.println("classnameAttribute: " + classnameAttribute.isString());

        attInfo.addElement(emg_0);
        attInfo.addElement(emg_1);
        attInfo.addElement(emg_2);
        attInfo.addElement(emg_3);
        attInfo.addElement(emg_4);
        attInfo.addElement(emg_5);
        attInfo.addElement(emg_6);
        attInfo.addElement(emg_7);

        attInfo.addElement(classnameAttribute);

        int capacity = 0;
        for (Recording recording : recordings) {
            capacity += recording.getEmgData().size();
        }

        Instances instances = new Instances("emg-data", attInfo, capacity / 8);
        for (Recording recording : recordings) {
            List<List<Byte>> averagesList = new ArrayList<>();

            for (int i = 0; i < EMGData.NUM_EMG_PADS; i++) {
                Stream<Byte> stream = recording.getEmgData().getData(i).stream();
                if (envelopeFollowerFilter != null) {
                    stream = envelopeFollowerFilter.apply(stream);
                }

                if (averageFilter != null) {
                    stream = averageFilter.apply(stream);
                }

                if (labelFilter != null) {
                    stream = labelFilter.apply(stream);
                }
                averagesList.add(stream.collect(Collectors.toList()));
            }

            for (int i = 0; i < averagesList.size(); i++) {
                Instance row = new Instance(attInfo.size());
                row.setValue(emg_0, averagesList.get(0).get(i));
                row.setValue(emg_1, averagesList.get(1).get(i));
                row.setValue(emg_2, averagesList.get(2).get(i));
                row.setValue(emg_3, averagesList.get(3).get(i));
                row.setValue(emg_4, averagesList.get(4).get(i));
                row.setValue(emg_5, averagesList.get(5).get(i));
                row.setValue(emg_6, averagesList.get(6).get(i));
                row.setValue(emg_7, averagesList.get(7).get(i));
                //System.out.println("recording.getGesture().getName(): " + recording.getGesture().getName());
                row.setValue(classnameAttribute, recording.getGesture().getName());
                instances.add(row);
            }
        }

        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    public String testAllClasses(Instances train, List<Instances> testList, Classifier cls) {
        String result = "";
        double biggestPctCorrect = -1;
        ExecutorService executor = Executors.newFixedThreadPool(testList.size());
        Set<FutureTask<Pair<String, Double>>> set = new HashSet<>();
        for (Instances testInstances : testList) {
            FutureTask<Pair<String, Double>> doubleFuture = new FutureTask<>(() -> test(train, testInstances, cls));
            set.add(doubleFuture);
            executor.execute(doubleFuture);
        }
        for (FutureTask<Pair<String, Double>> future : set) {
            Pair<String, Double> ptcCorrect = null;
            try {
                ptcCorrect = future.get();
                if (ptcCorrect.getRight() > biggestPctCorrect) {
                    biggestPctCorrect = ptcCorrect.getRight();
                    result = ptcCorrect.getLeft();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    public String testAllClassesSynchronously(Instances train, List<Instances> testList, Classifier cls) throws Exception {
        String result = "";
        double biggestPctCorrect = -1;
        for (Instances testInstances : testList) {
            Pair<String, Double> ptcCorrect = test(train, testInstances, cls);
            if (ptcCorrect.getRight() > biggestPctCorrect) {
                biggestPctCorrect = ptcCorrect.getRight();
                result = ptcCorrect.getLeft();
            }
        }
        return result;
    }

    private Pair<String, Double> test(Instances train, Instances test, Classifier cls) throws Exception {
        cls.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        //eval.crossValidateModel(cls, train, 10, new Random(1));
        //System.out.println("eval: " + eval == null);
        //System.out.println("cls: " + cls == null);
        //System.out.println("test: " + test == null);
        eval.evaluateModel(cls, test);
        //Pair<String, Double> resPair = Pair.of(test.instance(0).stringValue(8), eval.pctCorrect());
        //System.out.println("resPair" + resPair);
        return Pair.of(test.instance(0).stringValue(8), eval.pctCorrect());
    }

//    private List<String> getAllGestures() {
//        return gestureService.findAll().stream().map(Gesture::getName).collect(Collectors.toList());
//    }


    public static class Builder {

        private AverageFilter averageFilter;
        private LabelFilter labelFilter;
        private EnvelopeFollowerFilter envelopeFollowerFilter;

        public Builder() {
        }

        public Builder setAverageFilter(AverageFilter averageFilter) {
            this.averageFilter = averageFilter;
            return this;
        }

        public Builder setLabelFilter(LabelFilter labelFilter) {
            this.labelFilter = labelFilter;
            return this;
        }

        public Builder setEnvelopeFollowerFilter(EnvelopeFollowerFilter envelopeFollowerFilter) {
            this.envelopeFollowerFilter = envelopeFollowerFilter;
            return this;
        }

        public WekaTool build() {
            return new WekaTool(this);
        }
    }
}
