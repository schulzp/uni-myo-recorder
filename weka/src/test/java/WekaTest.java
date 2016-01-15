import edu.crimpbit.Converter;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.Stemmer;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by peter on 11/01/16.
 */
public class WekaTest {

    @Test
    public void converstion() {
        int attributeIndex = 0;
        Attribute emg_0 = new Attribute("emg_0", 0);
        Attribute emg_1 = new Attribute("emg_1", 1);

        FastVector attributeValues = new FastVector();
        attributeValues.addElement("exercise-a");
        attributeValues.addElement("exercise-b");
        attributeValues.addElement("exercise-c");

        Attribute clazz = new Attribute("exercise", attributeValues, 2);

        FastVector attInfo = new FastVector();
        attInfo.addElement(emg_0);
        attInfo.addElement(emg_1);
        attInfo.addElement(clazz);

        Instances instances = new Instances("emg-data", attInfo, 1);
        instances.setClass(clazz);

        Instance row = new Instance(3);

        row.setValue(emg_0, 10);
        row.setValue(emg_1, 15);
        row.setValue(clazz, "exercise-a");

        instances.add(row);
    }

    @Test
    public void conversion2() {
/*
        Stream<Double> d = Stream.of(new Double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0,
                5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.0,
                1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0});
        */

        Stream<Double> d = Stream.of(new Double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0});
        Instances instances = Converter.convert(d, "classname");

        System.out.println(instances);
        //System.out.println(instances.trainCV(instances.numInstances(), instances.numInstances()));

    }


}
