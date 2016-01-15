package edu.crimpbit;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Converter {

    public static Instances convert(Stream<Double> emgs, String classname) {
        List<Double> bytes = emgs.collect(Collectors.toList());
        //bytes.addAll(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7}));

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
        fastVector.addElement("classname");
        Attribute classnameAttribute = new Attribute("classname", fastVector, 8);


        attInfo.addElement(emg_0);
        attInfo.addElement(emg_1);
        attInfo.addElement(emg_2);
        attInfo.addElement(emg_3);
        attInfo.addElement(emg_4);
        attInfo.addElement(emg_5);
        attInfo.addElement(emg_6);
        attInfo.addElement(emg_7);

        attInfo.addElement(classname);

        //bytes.size()/8
        Instances instances = new Instances("emg-data", attInfo, bytes.size() / 8);
        for (int i = 0; i <= (bytes.size() - 8); i += 8) {
            Instance row = new Instance(attInfo.size());
            row.setValue(emg_0, bytes.get(i));
            row.setValue(emg_1, bytes.get(i + 1));
            row.setValue(emg_2, bytes.get(i + 2));
            row.setValue(emg_3, bytes.get(i + 3));
            row.setValue(emg_4, bytes.get(i + 4));
            row.setValue(emg_5, bytes.get(i + 5));
            row.setValue(emg_6, bytes.get(i + 6));
            row.setValue(emg_7, bytes.get(i + 7));
            row.setValue(classnameAttribute, classname);
            instances.add(row);
        }
        return instances;
    }

}
