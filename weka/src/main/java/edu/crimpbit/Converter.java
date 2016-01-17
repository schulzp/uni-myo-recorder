package edu.crimpbit;

import com.sun.istack.internal.NotNull;
import com.sun.org.apache.regexp.internal.RE;
import edu.crimpbit.filter.AverageFilter;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Converter {

    public static Instances convert(Recording recording) {
        System.out.println("Exercise: " + recording.getExercise());
        //System.out.println("recording.getEmgData().getData(0): " + recording.getEmgData().getData(0));


        //recording.getEmgData().stream().forEach(integerListEntry -> System.out.println(integerListEntry));

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
        fastVector.addElement("none");
        fastVector.addElement("weak");
        fastVector.addElement("strong");
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

        Instances instances = new Instances("emg-data", attInfo, recording.getEmgData().size() / 8);

        for (int i = 0; i < recording.getEmgData().size(); i++) {
            Instance row = new Instance(attInfo.size());
            row.setValue(emg_0, recording.getEmgData().getData(0).get(i));
            row.setValue(emg_1, recording.getEmgData().getData(1).get(i));
            row.setValue(emg_2, recording.getEmgData().getData(2).get(i));
            row.setValue(emg_3, recording.getEmgData().getData(3).get(i));
            row.setValue(emg_4, recording.getEmgData().getData(4).get(i));
            row.setValue(emg_5, recording.getEmgData().getData(5).get(i));
            row.setValue(emg_6, recording.getEmgData().getData(6).get(i));
            row.setValue(emg_7, recording.getEmgData().getData(7).get(i));
            row.setValue(classnameAttribute, "none");
            instances.add(row);
        }
        instances.setClassIndex(instances.numAttributes() - 1);

        return instances;
    }

    /**
     * Instance[a0,...,a199,{index-finger, index-finger+middle-finger, index-finger+middle-finger+ring-finger}]
     *
     * @param recordings
     * @return Instances
     */
//    public static Instances convert(@NotNull List<Recording> recordings) {
//        Instances instances = null;
//        for (Recording recording : recordings) {
//            FastVector attInfo = new FastVector();
//            for (int i = 0; i < 200; i++) {
//                Attribute attribute = new Attribute("a" + i, i);
//                attInfo.addElement(attribute);
//            }
//            FastVector fastVector = new FastVector();
//            fastVector.addElement("index-finger");
//            fastVector.addElement("index-finger+middle-finger");
//            fastVector.addElement("index-finger+middle-finger+ring-finger");
//            Attribute classnameAttribute = new Attribute("grip_type", fastVector, 200);
//            attInfo.addElement(classnameAttribute);
//            instances = new Instances(recording.getExercise(), attInfo, recording.getEmgData().size() / 8);
//
//            for (int i = 0; i < 200; i++) {
//                Instance row = new Instance(attInfo.size());
//                for (int j = 0; j < EMGData.NUM_EMG_PADS; j++) {
//                    //recording.getEmgData().getData(j).
//                    row.setValue(instances.attribute("a" + i), recording.getEmgData().getData(j).get(i));
//                    instances.add(row);
//                }
//                row.setValue(classnameAttribute, recording.getExercise());
//            }
//
//        }
//        instances.setClassIndex(instances.numAttributes() - 1);
//        return instances;
//    }

//    public static Instances convert(@NotNull List<Recording> recordings) {
//        Instances instances = null;
//        for (Recording recording : recordings) {
//            FastVector attInfo = new FastVector();
//            for (int i = 0; i < recording.getEmgData().size(); i++) {
//                Attribute attribute = new Attribute("a" + i, i);
//                attInfo.addElement(attribute);
//            }
//            FastVector fastVector = new FastVector();
//            fastVector.addElement("classname");
//            Attribute classnameAttribute = new Attribute("classname", fastVector, recording.getEmgData().size() + 1);
//            attInfo.addElement(classnameAttribute);
//            instances = new Instances(recording.getExercise(), attInfo, recording.getEmgData().size() / 8);
//            for (int i = 0; i < recording.getEmgData().size(); i++) {
//
//                for (int j = 0; j < EMGData.NUM_EMG_PADS; j++) {
//                    Instance row = new Instance(attInfo.size());
//                    row.setValue(instances.attribute("a" + i), recording.getEmgData().getData(j).get(i));
//                    instances.add(row);
//                }
//            }
//
//        }
//        instances.setClassIndex(instances.numAttributes() - 1);
//        return instances;
//    }

//    public static Instances convert(Stream<Double> emgs, String classname) {
//        List<Double> bytes = emgs.collect(Collectors.toList());
//
//        FastVector attInfo = new FastVector();
//
//        Attribute emg_0 = new Attribute("emg_0", 0);
//        Attribute emg_1 = new Attribute("emg_1", 1);
//        Attribute emg_2 = new Attribute("emg_2", 2);
//        Attribute emg_3 = new Attribute("emg_3", 3);
//        Attribute emg_4 = new Attribute("emg_4", 4);
//        Attribute emg_5 = new Attribute("emg_5", 5);
//        Attribute emg_6 = new Attribute("emg_6", 6);
//        Attribute emg_7 = new Attribute("emg_7", 7);
//
//        FastVector fastVector = new FastVector();
//        fastVector.addElement("classname");
//        Attribute classnameAttribute = new Attribute("classname", fastVector, 8);
//
//        attInfo.addElement(emg_0);
//        attInfo.addElement(emg_1);
//        attInfo.addElement(emg_2);
//        attInfo.addElement(emg_3);
//        attInfo.addElement(emg_4);
//        attInfo.addElement(emg_5);
//        attInfo.addElement(emg_6);
//        attInfo.addElement(emg_7);
//
//        attInfo.addElement(classnameAttribute);
//
//        Instances instances = new Instances("emg-data", attInfo, bytes.size() / 8);
//        for (int i = 0; i <= (bytes.size() - 8); i += 8) {
//            Instance row = new Instance(attInfo.size());
//            row.setValue(emg_0, bytes.get(i));
//            row.setValue(emg_1, bytes.get(i + 1));
//            row.setValue(emg_2, bytes.get(i + 2));
//            row.setValue(emg_3, bytes.get(i + 3));
//            row.setValue(emg_4, bytes.get(i + 4));
//            row.setValue(emg_5, bytes.get(i + 5));
//            row.setValue(emg_6, bytes.get(i + 6));
//            row.setValue(emg_7, bytes.get(i + 7));
//            row.setValue(classnameAttribute, classname);
//            instances.add(row);
//        }
//        instances.setClassIndex(instances.numAttributes() - 1);
//        return instances;
//    }

}
