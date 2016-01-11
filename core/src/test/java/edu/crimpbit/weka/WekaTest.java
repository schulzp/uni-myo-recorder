package edu.crimpbit.weka;

import edu.crimpbit.filter.AverageFilter;
import org.junit.Test;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by joerg on 11.01.16.
 */
public class WekaTest {

   /* @Test
    public void wekaTest1() throws Exception {
        AverageFilter filter = new AverageFilter(1);
        List<Byte> bytes = new ArrayList<>();
        bytes.addAll(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5, 6, 7}));
        Stream<Double> result = filter.apply(bytes.stream());
        ArrayList<Attribute> attr = new ArrayList<>();
        List<String> values = new ArrayList<>();
        result.forEach(d->values.add(d.toString()));
        attr.add(new Attribute("asd", values));
        DataSource dataSource = new DataSource(result.);
        Instances instances = dataSource.getDataSet();

    }*/

    @Test
    public void wekaTest() {

        Instance instance = new Instance(2);

        Attribute emg1 = new Attribute("emg1",0);
        Attribute emg2 = new Attribute("emg1",1);
        ArrayList<Attribute> attributes =  new ArrayList<>();
        attributes.add(emg1);
        attributes.add(emg2);
        instance.setValue(emg1, 3.2);
        instance.setValue(emg2, 3.2);
        Instances instances = instance.dataset();
        Instances instances1 = new Instances();
        System.out.println(instance);
        System.out.println(instances);
        //Instances instances = new Instances("bla", attributes, 2);



    }
}
