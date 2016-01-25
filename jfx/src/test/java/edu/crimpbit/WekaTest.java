package edu.crimpbit;

import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.RecordingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

    @Autowired
    private GestureService gestureService;

    private List<String> getAllGestures() {
        return gestureService.findAll().stream().map(Gesture::getName).collect(Collectors.toList());
    }

    @Test
    public void testGetSizes() throws Exception {
        for (Recording r : recordingService.findAll()) {
            System.out.println(r.getEmgData().size());
        }
    }


    /**
     * variables: subject, tags, gestures, filter, Classifier
     *
     *
     *
     * 1. Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     * 2. for each gesture with different sets of subjects
     * 3. for each gesture with different tags and different sets of subjects
     * 4.
     *
     * 1. tests für alle subjects
     * 2. tests für alle tags
     * 3.
     *
     *
     */


    /**
     * 1. Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     */
    @Test
    public void classifierTest() {
        //TODO filter subject (recording.getSubject().getName().equals("Peter") &&)
        List<Recording> trainList = recordingService.findAll().stream().filter(recording ->
                recording.getGesture().getName().equals("index") &&
                        recording.getGesture().getTags().get(0).equals("pull-downwards")
        ).collect(Collectors.toList());
        //List<Recording> testList = new ArrayList<>();
        Recording correctRecording = trainList.remove(0);
        //testList.add(correctRecording);
        WekaTool wekaTool = new WekaTool.Builder()
                .setAverageFilter(new AverageFilter(10))
                .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                .setLabelFilter(new LabelFilter(10)).build();





        Instances train = wekaTool.convertToTrainSet(trainList);

        Instances test = wekaTool.convertToTestSet(correctRecording,gestureService.findAll());


 //       System.out.println(train);

        System.out.println("--------------");
        System.out.println(test);
        System.out.println(train.numInstances());
        System.out.println(test.numInstances());

    }


    @Test
    public void testRecording1() throws ExecutionException, InterruptedException {
        List<Recording> trainList = recordingService.findAll();

        WekaTool wekaTool = new WekaTool.Builder()
                .setAverageFilter(new AverageFilter(10))
                .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                .setLabelFilter(new LabelFilter(10)).build();

        Recording testRecordingA = trainList.remove(0);
        Instances train = wekaTool.convert(trainList);

        List<Recording> testAList = new ArrayList<>();
        List<Recording> testBList = new ArrayList<>();
        List<Recording> testCList = new ArrayList<>();
        List<Recording> testDList = new ArrayList<>();
        List<Recording> testEList = new ArrayList<>();
        List<Recording> testFList = new ArrayList<>();
        List<Instances> testList = new ArrayList<>();

        Recording testRecordingB = testRecordingA;
        Recording testRecordingC = testRecordingA;
        Recording testRecordingD = testRecordingA;
        Recording testRecordingE = testRecordingA;
        Recording testRecordingF = testRecordingA;
        String actualClass = testRecordingA.getGesture().getName();


        testRecordingA.setGesture(new Gesture("index"));
        testAList.add(testRecordingA);
        testList.add(wekaTool.convert(testAList));

        testRecordingB.setGesture(new Gesture("index+middle"));
        testBList.add(testRecordingB);
        testList.add(wekaTool.convert(testBList));

        testRecordingC.setGesture(new Gesture("index+middle+ring"));
        testCList.add(testRecordingC);
        testList.add(wekaTool.convert(testCList));

        testRecordingD.setGesture(new Gesture("index+thumb"));
        testDList.add(testRecordingD);
        testList.add(wekaTool.convert(testDList));

        testRecordingE.setGesture(new Gesture("index+middle+thumb"));
        testEList.add(testRecordingE);
        testList.add(wekaTool.convert(testEList));

        testRecordingF.setGesture(new Gesture("index+middle+ring+thumb"));
        testFList.add(testRecordingF);
        testList.add(wekaTool.convert(testFList));


        System.out.println("Logistic: ");
        Logistic cls = new Logistic();
        String prediction = WekaTool.testAllClasses(train, testList, cls);
        System.out.println("---------------------------------");
        System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClass);
        System.out.println("Prediction correct: " + prediction.equals(actualClass));

    }

    @Test
    public void testRecording2() throws ExecutionException, InterruptedException {
        List<Recording> trainList = recordingService.findAll();
        Recording testRecordingA = trainList.remove(0);
        WekaTool wekaTool = new WekaTool.Builder()
                .setAverageFilter(new AverageFilter(10))
                .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                .setLabelFilter(new LabelFilter(10)).build();
        Instances train = wekaTool.convert(trainList);

        List<Recording> testAList = new ArrayList<>();
        List<Recording> testBList = new ArrayList<>();
        List<Recording> testCList = new ArrayList<>();
        List<Recording> testDList = new ArrayList<>();
        List<Recording> testEList = new ArrayList<>();
        List<Recording> testFList = new ArrayList<>();
        List<Instances> testList = new ArrayList<>();

        Recording testRecordingB = testRecordingA;
        Recording testRecordingC = testRecordingA;
        Recording testRecordingD = testRecordingA;
        Recording testRecordingE = testRecordingA;
        Recording testRecordingF = testRecordingA;
        String actualClass = testRecordingA.getGesture().getName();


        testRecordingA.setGesture(new Gesture("index"));
        testAList.add(testRecordingA);
        testList.add(wekaTool.convert(testAList));

        testRecordingB.setGesture(new Gesture("index+middle"));
        testBList.add(testRecordingB);
        testList.add(wekaTool.convert(testBList));

        testRecordingC.setGesture(new Gesture("index+middle+ring"));
        testCList.add(testRecordingC);
        testList.add(wekaTool.convert(testCList));

        testRecordingD.setGesture(new Gesture("index+thumb"));
        testDList.add(testRecordingD);
        testList.add(wekaTool.convert(testDList));

        testRecordingE.setGesture(new Gesture("index+middle+thumb"));
        testEList.add(testRecordingE);
        testList.add(wekaTool.convert(testEList));

        testRecordingF.setGesture(new Gesture("index+middle+ring+thumb"));
        testFList.add(testRecordingF);
        testList.add(wekaTool.convert(testFList));


        System.out.println("Logistic: ");
        Logistic cls = new Logistic();
        String prediction = WekaTool.testAllClasses(train, testList, cls);
        System.out.println("---------------------------------");
        System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClass);
        System.out.println("Prediction correct: " + prediction.equals(actualClass));

    }

    @Test
    public void testAllRecordingsWithWeka() throws ExecutionException, InterruptedException {
        List<Recording> trainList = recordingService.findAll();
        WekaTool wekaTool = new WekaTool.Builder()
                .setAverageFilter(new AverageFilter(10))
                .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                .setLabelFilter(new LabelFilter(10)).build();
        int logisticCounter = 0;
        int j48Counter = 0;
        int multiCounter = 0;
        trainList.remove(0);
        trainList.remove(0);
        for (int i = 0; i < trainList.size(); i++) {

            Recording testRecordingA = trainList.remove(i);

            Instances train = wekaTool.convert(trainList);
            //System.out.println(train);
            List<Recording> testAList = new ArrayList<>();
            List<Recording> testBList = new ArrayList<>();
            List<Recording> testCList = new ArrayList<>();
            List<Recording> testDList = new ArrayList<>();
            List<Recording> testEList = new ArrayList<>();
            List<Recording> testFList = new ArrayList<>();
            List<Instances> testList = new ArrayList<>();
            String actualClass = testRecordingA.getGesture().getName();
            Recording testRecordingB = testRecordingA;
            Recording testRecordingC = testRecordingA;
            Recording testRecordingD = testRecordingA;
            Recording testRecordingE = testRecordingA;
            Recording testRecordingF = testRecordingA;


//
//            fastVector.addElement("index");
//            fastVector.addElement("index+middle");
//            fastVector.addElement("index+middle+ring");
//            fastVector.addElement("index+thumb");
//            fastVector.addElement("index+middle+thumb");
//            fastVector.addElement("index+middle+ring+thumb");

            testRecordingA.setGesture(new Gesture("index"));
            testAList.add(testRecordingA);
            testList.add(wekaTool.convert(testAList));

            testRecordingB.setGesture(new Gesture("index+middle"));
            testBList.add(testRecordingB);
            testList.add(wekaTool.convert(testBList));

            testRecordingC.setGesture(new Gesture("index+middle+ring"));
            testCList.add(testRecordingC);
            testList.add(wekaTool.convert(testCList));

            testRecordingD.setGesture(new Gesture("index+thumb"));
            testDList.add(testRecordingD);
            testList.add(wekaTool.convert(testDList));

            testRecordingE.setGesture(new Gesture("index+middle+thumb"));
            testEList.add(testRecordingE);
            testList.add(wekaTool.convert(testEList));

            testRecordingF.setGesture(new Gesture("index+middle+ring+thumb"));
            testFList.add(testRecordingF);
            testList.add(wekaTool.convert(testFList));

            System.out.println("Test " + i + " of " + trainList.size());
            System.out.println("Logistic: ");
            Logistic cls = new Logistic();
            String prediction = WekaTool.testAllClasses(train, testList, cls);
            System.out.println("---------------------------------");
            System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClass);
            System.out.println("Prediction correct: " + prediction.equals(actualClass));
            if (prediction.equals(actualClass))
                logisticCounter++;

//            System.out.println();
//            System.out.println("J48: ");
//            J48 cls2 = new J48();
//            String prediction2 = WekaTool.testAllClasses(train, testList, cls2);
//            System.out.println("---------------------------------");
//            System.out.println("Predicted Class: " + prediction2 + " Actual Class : " + actualClass);
//            System.out.println("Prediction correct: " + prediction2.equals(actualClass));
//            if (prediction2.equals(actualClass))
//                j48Counter++;
//
//
//            System.out.println();
//            System.out.println("MultilayerPerceptron: ");
//            MultilayerPerceptron cls3 = new MultilayerPerceptron();
//            String prediction3 = WekaTool.testAllClasses(train, testList, cls3);
//            System.out.println("---------------------------------");
//            System.out.println("Predicted Class: " + prediction3 + " Actual Class : " + actualClass);
//            System.out.println("Prediction correct: " + prediction3.equals(actualClass));
//            if (prediction3.equals(actualClass))
//                multiCounter++;

            trainList.add(i, testRecordingA);
        }
        System.out.println("logisticCounter: " + logisticCounter);
        System.out.println("j48Counter: " + j48Counter);
        System.out.println("multiCounter: " + multiCounter);
        System.out.println("total: " + trainList.size());

    }

}
