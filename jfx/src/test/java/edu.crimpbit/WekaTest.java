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
    public void testGetSizes() throws Exception {
        for (Recording r : recordingService.findAll()) {
            System.out.println(r.getEmgData().size());
        }
    }

    @Test
    public void testAllRecordingsWithWeka() {
        List<Recording> trainList = recordingService.findAll();

        int logisticCounter = 0;
        int j48Counter = 0;
        int multiCounter = 0;
        trainList.remove(0);
        trainList.remove(0);
        for (int i = 0; i < trainList.size(); i++) {

            Recording testRecordingA = trainList.remove(i);

            Instances train = WekaTool.convert(trainList);
            System.out.println(train);
            List<Recording> testAList = new ArrayList<>();
            List<Recording> testBList = new ArrayList<>();
            List<Recording> testCList = new ArrayList<>();
            List<Instances> testList = new ArrayList<>();
            String actualClass = testRecordingA.getExercise();
            Recording testRecordingB = testRecordingA;
            Recording testRecordingC = testRecordingA;

            testRecordingA.setExercise("index-finger");
            testAList.add(testRecordingA);
            testList.add(WekaTool.convert(testAList));

            testRecordingB.setExercise("index-finger+middle-finger");
            testBList.add(testRecordingB);
            testList.add(WekaTool.convert(testBList));

            testRecordingC.setExercise("index-finger+middle-finger+ring-finger");
            testCList.add(testRecordingC);
            testList.add(WekaTool.convert(testCList));

            System.out.println("Logistic: ");
            Logistic cls = new Logistic();
            String prediction = WekaTool.testAllClasses(train, testList, cls);
            System.out.println("---------------------------------");
            System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClass);
            System.out.println("Prediction correct: " + prediction.equals(actualClass));
            if (prediction.equals(actualClass))
                logisticCounter++;

            System.out.println();
            System.out.println("J48: ");
            J48 cls2 = new J48();
            String prediction2 = WekaTool.testAllClasses(train, testList, cls2);
            System.out.println("---------------------------------");
            System.out.println("Predicted Class: " + prediction2 + " Actual Class : " + actualClass);
            System.out.println("Prediction correct: " + prediction2.equals(actualClass));
            if (prediction2.equals(actualClass))
                j48Counter++;


            System.out.println();
            System.out.println("MultilayerPerceptron: ");
            MultilayerPerceptron cls3 = new MultilayerPerceptron();
            String prediction3 = WekaTool.testAllClasses(train, testList, cls3);
            System.out.println("---------------------------------");
            System.out.println("Predicted Class: " + prediction3 + " Actual Class : " + actualClass);
            System.out.println("Prediction correct: " + prediction3.equals(actualClass));
            if (prediction3.equals(actualClass))
                multiCounter++;

            trainList.add(i, testRecordingA);
        }
        System.out.println("logisticCounter: " + logisticCounter);
        System.out.println("j48Counter: " + j48Counter);
        System.out.println("multiCounter: " + multiCounter);
        System.out.println("total: " + trainList.size());

    }
}
