import edu.crimpbit.Converter;
import org.junit.Test;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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


        //Stream<Double> d = Stream.of(new Double[]{0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0});
        //Instances instances = Converter.converter(new Random().doubles(16, 127, 255).boxed(), "classname");

        //System.out.println("unClassified");
        //System.out.println(instances);
        //System.out.println(instances.trainCV(instances.numInstances(), instances.numInstances()));
        //Logistic logistic = new Logistic();
        //J48 j48 = new J48();
        //MultilayerPerceptron mp = new MultilayerPerceptron();
        //try {
        //    j48.buildClassifier(instances);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //System.out.println("j48");
        //System.out.println(instances);


    }

    public void wekaLogisticTest() {
//        List<Double> emg0 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
//        List<Double> emg1 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
//        List<Double> emg2 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
//        List<Double> emg3 = Arrays.asList(new Double[]{0.0, 0.0, 0.2, 0.4, 0.2, 0.0, 0.0, 0.0});
//        List<Double> emg4 = Arrays.asList(new Double[]{0.0, 0.0, 0.2, 0.6, 0.3, 0.0, 0.0, 0.0});
//        List<Double> emg5 = Arrays.asList(new Double[]{0.0, 0.0, 0.2, 0.4, 0.2, 0.0, 0.0, 0.0});
//        List<Double> emg6 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
//        List<Double> emg7 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        String name = "grip";

        // emg_1, .., emg_n, emg_number(0..7)
    }



}
