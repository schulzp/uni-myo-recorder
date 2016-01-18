package edu.crimpbit;

import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import one.util.streamex.EntryStream;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WekaTool {

    public static Instances convert(List<Recording> recordings) {
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
        fastVector.addElement("index-finger");
        fastVector.addElement("index-finger+middle-finger");
        fastVector.addElement("index-finger+middle-finger+ring-finger");
        Attribute classnameAttribute = new Attribute("grip_type", fastVector, 8);

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

        int chunk = 1;
        for (Recording recording : recordings) {
            EnvelopeFollowerFilter envelopeFollowerFilter = new EnvelopeFollowerFilter(0.3, 0.8);
            //envelopeFollowerFilter.apply(EntryStream.of(recording.getEmgData().getData(0).stream().map(Byte::doubleValue).collect(Collectors.toList())));
            LabelFilter labelFilter = new LabelFilter(10);
            AverageFilter averageFilter = new AverageFilter(10);
            List<List<Byte>> averagesList = new ArrayList<>();
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(0).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(1).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(2).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(3).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(4).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(5).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(6).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(7).stream())).collect(Collectors.toList()));
//

            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(0).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(1).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(2).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(3).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(4).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(5).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(6).stream()))).collect(Collectors.toList()));
            averagesList.add(labelFilter.apply(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(7).stream()))).collect(Collectors.toList()));

//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(0).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(1).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(2).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(3).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(4).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(5).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(6).stream())).collect(Collectors.toList()));
//            averagesList.add(averageFilter.apply(envelopeFollowerFilter.apply(recording.getEmgData().getData(7).stream())).collect(Collectors.toList()));

//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(0).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(1).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(2).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(3).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(4).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(5).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(6).stream()).collect(Collectors.toList()));
//            averagesList.add(envelopeFollowerFilter.apply(recording.getEmgData().getData(7).stream()).collect(Collectors.toList()));
//



//            averagesList.add(
//                    labelFilter.apply(averageFilter.apply(
//                            envelopeFollowerFilter.apply(recording.getEmgData().getData(0).stream()
//                                            .map(Byte::doubleValue)
//                                            .collect(Collectors.toList())))
//                                    .map(entry -> entry.getValue().byteValue()))))));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(1).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(2).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(3).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(4).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(5).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(6).stream())).collect(Collectors.toList()));
//            averagesList.add(labelFilter.apply(averageFilter.apply(recording.getEmgData().getData(7).stream())).collect(Collectors.toList()));
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
                row.setValue(classnameAttribute, recording.getExercise());
                instances.add(row);
            }
        }

        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    public static String testAllClasses(Instances train, List<Instances> testList, Classifier cls) {
        String result = "";
        double biggestPctCorrect = -1;
        for (Instances testInstances : testList) {
            double ptcCorrect = test(train, testInstances, cls);
            if (ptcCorrect > biggestPctCorrect) {
                biggestPctCorrect = ptcCorrect;
                result = testInstances.instance(0).stringValue(8);
            }
        }
        return result;
    }

    private static double test(Instances train, Instances test, Classifier cls) {
        try {
            cls.buildClassifier(train);
            Evaluation eval = new Evaluation(train);
            eval.crossValidateModel(cls, train, 10, new Random(1));
            eval.evaluateModel(cls, test);
            return eval.pctCorrect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
