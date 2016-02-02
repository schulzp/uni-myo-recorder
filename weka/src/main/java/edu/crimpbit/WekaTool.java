package edu.crimpbit;

import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
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
        return convert(recordings, gestures, false);
    }

    public Instances convertToTestSet(Recording recording, List<Gesture> gestures) {

        return convert(Arrays.asList(recording), gestures, true);
    }

    public Instances convert(List<Recording> recordings, List<Gesture> gestures, boolean isTestSet) {
        FastVector attInfo = new FastVector();

        Attribute emg_0 = new Attribute("emg_0", 0);
        Attribute emg_1 = new Attribute("emg_1", 1);
        Attribute emg_2 = new Attribute("emg_2", 2);
        Attribute emg_3 = new Attribute("emg_3", 3);
        Attribute emg_4 = new Attribute("emg_4", 4);
        Attribute emg_5 = new Attribute("emg_5", 5);
        Attribute emg_6 = new Attribute("emg_6", 6);
        Attribute emg_7 = new Attribute("emg_7", 7);

        FastVector classVal = new FastVector();


        for (Gesture gesture : gestures) {
            classVal.addElement(gesture.getName());
        }

        Attribute classnameAttribute = new Attribute("grip_type", classVal, 8);

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
        instances.setClassIndex(instances.numAttributes() - 1);
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
                row.setDataset(instances);
                row.setValue(emg_0, averagesList.get(0).get(i));
                row.setValue(emg_1, averagesList.get(1).get(i));
                row.setValue(emg_2, averagesList.get(2).get(i));
                row.setValue(emg_3, averagesList.get(3).get(i));
                row.setValue(emg_4, averagesList.get(4).get(i));
                row.setValue(emg_5, averagesList.get(5).get(i));
                row.setValue(emg_6, averagesList.get(6).get(i));
                row.setValue(emg_7, averagesList.get(7).get(i));
                if (isTestSet) {
                    //row.setValue(classnameAttribute, "?");
                } else {
                    row.setValue(8, recording.getGesture().getName());
                }
                instances.add(row);
            }
        }


        return instances;
    }

    public void crossValidate(Instances data, Classifier cls, List<Gesture> gestures) throws Exception {
        cls.buildClassifier(data);
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(cls, data, 10, new Random());
        System.out.println(eval.toSummaryString(true));
        for (int i = 0; i < eval.confusionMatrix().length; i++) {
            for (int j = 0; j < eval.confusionMatrix()[i].length; j++) {
                System.out.print(eval.confusionMatrix()[i][j] + " ");
            }
            System.out.print(gestures.get(i).getName());
            System.out.println();
        }
    }


    public String evaluate(Instances train, Instances test, Classifier cls, List<Gesture> gestures) throws Exception {
        cls.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        return evaluatePredictions(eval.evaluateModel(cls, test), gestures);
    }

    private String evaluatePredictions(double[] predictions, List<Gesture> gestures) {
        double sum = 0;
        for (double d : predictions)
            sum += d;
        double average = sum / predictions.length;
        if (average <= 1.0) {
            return gestures.get(0).getName();
        } else if (average > 1.0 && average <= 2.0) {
            return gestures.get(1).getName();
        } else if (average > 2.0 && average <= 3.0) {
            return gestures.get(2).getName();
        } else if (average > 3.0 && average <= 4.0) {
            return gestures.get(3).getName();
        } else if (average > 4.0 && average <= 5.0) {
            return gestures.get(4).getName();
        } else if (average > 5.0 && average <= 6.0) {
            return gestures.get(5).getName();
        }
        return null;
    }

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
