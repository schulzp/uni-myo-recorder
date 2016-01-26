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
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.mi.SimpleMI;
import weka.classifiers.misc.HyperPipes;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.*;
import java.util.concurrent.*;
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
     * <p>
     * <p>
     * <p>
     * 1. Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     * 2. for each gesture with different sets of subjects
     * 3. for each gesture with different tags and different sets of subjects
     * 4.
     * <p>
     * 1. tests für alle subjects
     * 2. tests für alle tags
     * 3.
     */


    private Integer[][] classify(String subjectName, String tag, String gesture, Classifier cls, int filterMin, int filterMax) throws ExecutionException, InterruptedException {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(subjectName, tag, gesture);
        List<String> actualClasses = new ArrayList<>();
        Integer[][] counterArray = new Integer[filterMax][filterMax];
        for (int i = 0; i < filterMax; i++) {
            for (int j = 0; j < filterMax; j++) {
                counterArray[i][j] = 0;
            }
        }
        for (int i = 0; i < trainList.size(); i++) {
            Recording correctRecording = trainList.remove(i);
            actualClasses.add(correctRecording.getGesture().getName());
            for (int j = filterMin; j < filterMax; j++) {
                for (int k = filterMin; k < filterMax; k++) {

                    WekaTool wekaTool = new WekaTool.Builder()
                            .setAverageFilter(new AverageFilter(j))
                            .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                            .setLabelFilter(new LabelFilter(k)).build();

                    Instances train = wekaTool.convertToTrainSet(trainList);
                    List<Instances> test = wekaTool.convertToTestSet(correctRecording, gestureService.findAll());
                    if (WekaTool.testAllClassesSynchronously(train, test, cls).equals(actualClasses.get(i))) {
                        System.out.println("correct i: " + i + " j: " + j + " k: " + k + " classifier: " + cls.getClass().getSimpleName());
                        counterArray[j][k]++;
                    }
                }
            }
            trainList.add(i, correctRecording);
        }
        return counterArray;

    }


    @Test
    public void classifierTest2() throws ExecutionException, InterruptedException {
        int filterMin = 2;
        int filterMax = 20;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        HashMap<String, FutureTask<Integer[][]>> map = new HashMap<>();
        //Set<FutureTask<Integer[][]>> set = new HashSet<>();

        FutureTask<Integer[][]> futureTask1 = new FutureTask<>(() -> classify("Peter", null, "index", new MultilayerPerceptron(), filterMin, filterMax));
        //FutureTask<Integer[][]> futureTask2 = new FutureTask<>(() -> classify("Peter", null, "index", new Logistic(), filterMin, filterMax));
        //FutureTask<Integer[][]> futureTask3 = new FutureTask<>(() -> classify("Peter", null, "index", new NaiveBayes(), filterMin, filterMax));

        map.put("MultilayerPerceptron", futureTask1);
        executor.execute(futureTask1);
        // map.put("Logistic", futureTask2);
        // executor.execute(futureTask2);
        // map.put("NaiveBayes", futureTask3);
        //executor.execute(futureTask3);

        for (Map.Entry<String, FutureTask<Integer[][]>> stringFutureTaskEntry : map.entrySet()) {
            System.out.println(stringFutureTaskEntry.getKey());
            Integer[][] data = stringFutureTaskEntry.getValue().get();
            for (int i = 2; i < 20; i++) {
                for (int j = 2; j < 20; j++) {
                    System.out.printf("%d, ", data[i][j]);
                }
                System.out.println();
            }
        }
    }

    /**
     * 1. Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     */
    @Test
    public void classifierTest() throws ExecutionException, InterruptedException {
        //TODO filter subject (recording.getSubject().getName().equals("Peter") &&)
//        List<Recording> trainList = recordingService.findAll().stream().filter(recording ->
//                recording.getGesture().getName().equals("index") &&
//                        recording.getGesture().getTags().get(0).equals("pull-downwards")
//        ).collect(Collectors.toList());

//        List<Recording> trainList = recordingService.findAll().stream().filter(recording ->
//                recording.getGesture().getName().equals("index")
//        ).collect(Collectors.toList());

        //System.out.println("id: " + trainList.get(0).getSubject().getId() + " name: " + trainList.get(0).getSubject().getName());
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture("Peter", null, "index");

        //List<Recording> trainList = recordingService.findAll();
        //System.out.println(trainList.get(0).getEmgData().getData(0));
        System.out.println("trainList.size(): " + trainList.size());
        int logisticCounter = 0;
        int j48Counter = 0;
        int multiCounter = 0;
        List<String> actualClasses = new ArrayList<>();
        int[][] counterArray = new int[21][21];

        for (int i = 0; i < trainList.size(); i++) {
            Recording correctRecording = trainList.remove(i);
            System.out.println("correctRecording.getGesture().getName(): " + correctRecording.getGesture().getName());
            actualClasses.add(correctRecording.getGesture().getName());

            //int j = 10;
            //int k = 10;
            for (int j = 1; j <= 20; j++) {
                for (int k = 1; k < 20; k++) {

                    WekaTool wekaTool = new WekaTool.Builder()
                            .setAverageFilter(new AverageFilter(j))
                            .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                            .setLabelFilter(new LabelFilter(k)).build();

                    //WekaTool wekaTool = new WekaTool();


                    Instances train = wekaTool.convertToTrainSet(trainList);
                    //System.out.println(train);
                    List<Instances> test = wekaTool.convertToTestSet(correctRecording, gestureService.findAll());

//                for (Instances instances : test) {
//                    System.out.println("+++++++++++++++++++++++++++++++");
//                    System.out.println(instances);
//                    System.out.println("+++++++++++++++++++++++++++++++");
//                }

                    System.out.println("i = " + i + ", j = " + j + ", k " + k);

                    //System.out.println("i = " + i + ", j = " + j);
//                System.out.println("---------------------------------");
//                Logistic cls = new Logistic();
//                //String prediction = WekaTool.testAllClasses(train, test, cls);
//                String prediction = WekaTool.testAllClassesSynchronously(train, test, cls);
//
//                System.out.println("Logistic");
//                System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClasses.get(i));
//                System.out.println("Prediction correct: " + prediction.equals(actualClasses.get(i)));
//                if (prediction.equals(actualClasses.get(i)))
//                    logisticCounter++;

                    Logistic cls2 = new Logistic();
                    //J48 cls2 = new J48();
                    //String prediction2 = WekaTool.testAllClasses(train, test, cls2);
                    String prediction2 = WekaTool.testAllClassesSynchronously(train, test, cls2);

                    System.out.println("Logistic");
                    //System.out.println("J48");
                    System.out.println("Predicted Class: " + prediction2 + " Actual Class : " + actualClasses.get(i));
                    System.out.println("Prediction correct: " + prediction2.equals(actualClasses.get(i)));
                    if (prediction2.equals(actualClasses.get(i))) {
                        counterArray[j][k]++;
                        //j48Counter++;
                        logisticCounter++;
                    }

//                MultilayerPerceptron cls3 = new MultilayerPerceptron();
//                //String prediction3 = WekaTool.testAllClasses(train, test, cls3);
//                String prediction3 = WekaTool.testAllClassesSynchronously(train, test, cls3);
//
//                System.out.println("MultilayerPerceptron");
//                System.out.println("Predicted Class: " + prediction3 + " Actual Class : " + actualClasses.get(i));
//                System.out.println("Prediction correct: " + prediction3.equals(actualClasses.get(i)));
//                if (prediction3.equals(actualClasses.get(i)))
//                    multiCounter++;
                    System.out.println("---------------------------------");

                }
            }
            trainList.add(i, correctRecording);
        }

        for (int i = 0; i < 20; i++) {
            //System.out.print("");
            //System.out.print("i " + i + ": ");
            for (int j = 0; j < 20; j++) {
                System.out.printf("%d, ", counterArray[i][j]);
            }
            System.out.println();
        }
        System.out.println("logisticCounter: " + logisticCounter);
        System.out.println("j48Counter: " + j48Counter);
        System.out.println("multiCounter: " + multiCounter);
        System.out.println("total: " + trainList.size());
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
