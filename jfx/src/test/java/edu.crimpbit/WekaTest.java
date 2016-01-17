package edu.crimpbit;

import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.service.RecordingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Dario on 16.01.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfiguration.class})
@Transactional
@Rollback
public class WekaTest {

    @Autowired
    RecordingService recordingService;

    @Test
    public void test1() throws Exception {
        Instances instances = Converter.convert(recordingService.findAll().get(4));
        System.out.println(instances);
        //Logistic logistic = new Logistic();
        //logistic.buildClassifier(instances);
    }

    @Test
    public void test2() throws Exception {
        for (Recording r : recordingService.findAll()) {
            System.out.println(r.getEmgData().size());
        }
    }

    @Test
    public void test3() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        recordings = recordings.stream()
                .filter(recording -> recording.getEmgData().size() >= 174).limit(recordings.size() - 1)
                .collect(Collectors.toList());

        List<Recording> testList = new ArrayList<>();
        testList.add(recordings.get(recordings.size() - 1));
        recordings.remove(recordings.size() - 1);
        Instances train = Converter.convert(recordings);
        Instances test = Converter.convert(testList);
        System.out.println(train);

        System.out.println("test instance: ");
        System.out.println(test.instance(0));
        Logistic cls = new Logistic();
        cls.buildClassifier(train);
        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(cls, test);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());
    }

    @Test
    public void test4() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        recordings = recordings.stream()
                .filter(recording -> recording.getEmgData().size() >= 174).limit(recordings.size() - 1)
                .collect(Collectors.toList());

        List<Recording> testList = new ArrayList<>();
        testList.add(recordings.get(recordings.size() - 1));
        recordings.remove(recordings.size() - 1);
        Instances train = Converter.convert(recordings);
        Instances test = Converter.convert(testList);
        System.out.println(train);

        System.out.println("test instance: ");
        System.out.println(test.instance(0));
        Logistic cls = new Logistic();
        cls.buildClassifier(train);
        Evaluation eval = new Evaluation(train);

        eval.evaluateModel(cls, test);
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());

        System.out.println("the probability of being positive:" + cls.distributionForInstance(test.instance(0))[0]);
        System.out.println("the probability of being negative:" + cls.distributionForInstance(test.instance(0))[1]);
    }

    @Test
    public void test10fold() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        recordings = recordings.stream()
                .filter(recording -> recording.getEmgData().size() >= 174)
                .collect(Collectors.toList());
        Instances train = Converter.convert(recordings);
        System.out.println(train);
        Logistic cls = new Logistic();
        //MultilayerPerceptron cls = new MultilayerPerceptron();
        //J48 cls = new J48();
        Evaluation eval = new Evaluation(train);
        eval.crossValidateModel(cls, train, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", true));
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());
    }


//    @Test
//    public void test3() throws Exception {
//        List<Recording> recordings = recordingService.findAll().stream().map(recording -> {
//            return new NormalizeFilter().apply(recording);
//        }).collect(Collectors.toList());
//        System.out.println(Converter.convert(recordings));
//    }
}
