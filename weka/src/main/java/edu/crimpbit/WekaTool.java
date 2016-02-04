package edu.crimpbit;

import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WekaTool {

    private final AverageFilter averageFilter;
    private final LabelFilter labelFilter;
    private final EnvelopeFollowerFilter envelopeFollowerFilter;

    private final DataSplitter<Byte> splitter;

    private final List<Gesture> gestures;

    public WekaTool(Builder builder) {
        averageFilter = builder.averageFilter;
        labelFilter = builder.labelFilter;
        splitter = builder.splitter;
        gestures = builder.gestures;
        envelopeFollowerFilter = builder.envelopeFollowerFilter;
    }

    public InstancesSupplier convertToTrainSet(List<Recording> recordings) {
        return convert(recordings, false);
    }

    public InstancesSupplier convertToTestSet(List<Recording> recordings) {
        return convert(recordings, true);
    }

    public InstancesSupplier convert(List<Recording> recordings, boolean isTestSet) {
        FastVector attInfo = new FastVector();

        int attributeIndex = 0;

        Attribute emg_0 = new Attribute("emg_0", attributeIndex++);
        Attribute emg_1 = new Attribute("emg_1", attributeIndex++);
        Attribute emg_2 = new Attribute("emg_2", attributeIndex++);
        Attribute emg_3 = new Attribute("emg_3", attributeIndex++);
        Attribute emg_4 = new Attribute("emg_4", attributeIndex++);
        Attribute emg_5 = new Attribute("emg_5", attributeIndex++);
        Attribute emg_6 = new Attribute("emg_6", attributeIndex++);
        Attribute emg_7 = new Attribute("emg_7", attributeIndex++);

        FastVector directionValues = new FastVector();

        recordings.stream()
                .map(Recording::getGesture)
                .flatMap(gesture -> gesture.getTags().stream())
                .collect(Collectors.toSet())
                .stream()
                .forEach(directionValues::addElement);

        Attribute direction = new Attribute("direction", directionValues, attributeIndex++);

        FastVector subjectValues = new FastVector();

        recordings.stream()
                .map(Recording::getSubject)
                .map(Subject::getName)
                .collect(Collectors.toSet())
                .stream()
                .forEach(subjectValues::addElement);

        Attribute subject = new Attribute("subject", subjectValues, attributeIndex++);

        FastVector classVal = new FastVector();

        for (Gesture gesture : this.gestures) {
            classVal.addElement(gesture.getName());
        }

        Attribute classnameAttribute = new Attribute("grip_type", classVal, attributeIndex++);

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
                Stream<Byte> stream = getEmgDataStream(recording, i, isTestSet);
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

            for (int i = 0; i < averagesList.get(0).size(); i++) {
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

                row.setValue(8, recording.getGesture().getName());

                instances.add(row);
            }
        }

        return new InstancesSupplier(instances);
    }



    private Stream<Byte> getEmgDataStream(Recording recording, int i, boolean isTestSet) {
        List<Byte> data = recording.getEmgData().getData(i);
        data = splitter.apply(data, isTestSet ? DataSplitter.DataType.TEST : DataSplitter.DataType.TRAINING);
        return data.stream();
    }

    public static Evaluation crossValidate(Instances data, Classifier cls) throws Exception {
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(cls, data, 2, new Random());
        return evaluation;
    }

    public static Evaluation evaluate(Instances testData, Classifier classifier) throws Exception {
        Evaluation evaluation = new Evaluation(testData);
        evaluation.evaluateModel(classifier, testData);
        return evaluation;
    }

    public static String evaluate(Instances train, Instances test, Classifier cls, List<Gesture> gestures) throws Exception {
        cls.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        return evaluatePredictions(eval.evaluateModel(cls, test), gestures);
    }

    private static String evaluatePredictions(double[] predictions, List<Gesture> gestures) {
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
        public DataSplitter<Byte> splitter = new DataSplitter<>();

        public List<Gesture> gestures;

        public Builder() {
        }

        public void setGestures(List<Gesture> gestures) {
            this.gestures = gestures;
        }

        public void setSplitter(DataSplitter<Byte> splitter) {
            this.splitter = splitter;
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
