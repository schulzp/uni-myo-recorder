package edu.crimpbit;

import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.RecordingService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
    private static PrintWriter writer = null;

    @BeforeClass
    public static void oneTimeSetUp() {

        String fileName = "WekaTest" + System.currentTimeMillis() + ".txt";
        try {
            writer = new PrintWriter(new FileOutputStream(new File(fileName), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        writer.close();
    }

    /**
     * variables: subject, tags, gestures, filter, Classifier
     * <p>
     * Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     */
//    @Test
//    public void differentFiltersAndClassifiersTest() throws ExecutionException, InterruptedException {
//        int averageFilterMin = 2;
//        int averageFilterMax = 20;
//        int labelFilterMin = 2;
//        int labelFilterMax = 20;
//        ExecutorService executor = Executors.newFixedThreadPool(4);
//        //ExecutorService executor = Executors.newFixedThreadPool(2);
//        HashMap<String, FutureTask<Integer[][]>> map = new HashMap<>();
//        //Set<FutureTask<Integer[][]>> set = new HashSet<>();
//
//        FutureTask<Integer[][]> futureTask1 = new FutureTask<>(() -> classify("Peter", null, "index", new J48(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//        FutureTask<Integer[][]> futureTask2 = new FutureTask<>(() -> classify("Peter", null, "index", new Logistic(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//        FutureTask<Integer[][]> futureTask3 = new FutureTask<>(() -> classify("Peter", null, "index", new NaiveBayes(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//        // FutureTask<Integer[][]> futureTask4 = new FutureTask<>(() -> classify("Peter", null, "index", new MultilayerPerceptron(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//
//
//        map.put("J48", futureTask1);
//        executor.execute(futureTask1);
//        map.put("Logistic", futureTask2);
//        executor.execute(futureTask2);
//        map.put("NaiveBayes", futureTask3);
//        executor.execute(futureTask3);
//
//        for (Map.Entry<String, FutureTask<Integer[][]>> stringFutureTaskEntry : map.entrySet()) {
//            String fileName = stringFutureTaskEntry.getKey() + System.currentTimeMillis() + ".txt";
//            printArray(stringFutureTaskEntry.getValue().get(), stringFutureTaskEntry.getKey(), fileName, averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax);
//            printBestFilterCombo(stringFutureTaskEntry.getValue().get(), stringFutureTaskEntry.getKey(), fileName, averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax);
//        }
//    }

    /**
     * variables: subject, tags, gestures, filter, Classifier
     * <p>
     * for each gesture with different sets of subjects
     */
//    @Test
//    public void differentSetsOfSubjectsTest() throws ExecutionException, InterruptedException {
//        int averageFilterMin = 19;
//        int averageFilterMax = 20;
//        int labelFilterMin = 3;
//        int labelFilterMax = 4;
//        ExecutorService executor = Executors.newFixedThreadPool(3);
//        HashMap<String, FutureTask<Integer[][]>> map = new HashMap<>();
//
//        FutureTask<Integer[][]> futureTask1 = new FutureTask<>(() -> classify("Peter", null, "index", new J48(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//        FutureTask<Integer[][]> futureTask2 = new FutureTask<>(() -> classify("Jasmin", null, "index", new J48(), averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax));
//
//        map.put("Peter", futureTask1);
//        executor.execute(futureTask1);
//        map.put("Jasmin", futureTask2);
//        executor.execute(futureTask2);
//
//        for (Map.Entry<String, FutureTask<Integer[][]>> stringFutureTaskEntry : map.entrySet()) {
//            String fileName = stringFutureTaskEntry.getKey() + System.currentTimeMillis() + ".txt";
//            printArray(stringFutureTaskEntry.getValue().get(), stringFutureTaskEntry.getKey(), fileName, averageFilterMin, averageFilterMax, labelFilterMin, labelFilterMax);
//        }
//    }

    /**
     * variables: subject, tags, gestures, filter, Classifier
     * <p>
     * for each gesture with different tags and different sets of subjects
     */
//    @Test
//    public void differentTagsAndDifferentSetsOfSubjects() {
//
//    }

    /**
     * Tests all recordings with all gestures and classifier J48
     *
     * @throws Exception
     */
    @Test
    public void testAllRecordingsWithJ48() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new J48(), 5, 18);
        writer.println("testAllRecordingsWithJ48 pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithJ48 pctCorrect: " + pctCorrect); //42.7083%
    }

    /**
     * Tests all recordings with all gestures and classifier NaiveBayes
     *
     * @throws Exception
     */
    @Test
    public void testAllRecordingsWithNaiveBayes() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new NaiveBayes(), 3, 18);
        writer.println("testAllRecordingsWithNaiveBayes pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithNaiveBayes pctCorrect: " + pctCorrect); //36.4583%

    }


    /**
     * Tests all recordings with all gestures and classifier Logistic
     *
     * @throws Exception
     */
    @Test
    public void testAllRecordingsWithLogistic() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new Logistic(), 19, 12);
        writer.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect); //46.875%

    }

    /**
     * Tests all recordings with all gestures and classifier MultilayerPerceptron
     *
     * @throws Exception
     */
    @Ignore("takes to long...")
    @Test
    public void testAllRecordingsWithMultilayerPerceptron() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new MultilayerPerceptron(), 15, 17);
        writer.println("testAllRecordingsWithMultilayerPerceptron pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithMultilayerPerceptron pctCorrect: " + pctCorrect); //31.25%

    }

    /**
     * Tests recordings with the gestures index,index+middle, index+middle+ring and classifier J48
     *
     * @throws Exception
     */
    @Test
    public void testSelectedRecordingsWithJ48() throws Exception {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(null, null, "index");
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle"));
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle+ring"));
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
        double pctCorrect = classifyAllRecordings(trainList, gestures, new J48(), 5, 18);
        writer.println("testSelectedRecordingsWithJ48 pctCorrect: " + pctCorrect);
        System.out.println("testSelectedRecordingsWithJ48 pctCorrect: " + pctCorrect); //91.66%
    }

    /**
     * Tests recordings with the gestures index,index+middle, index+middle+ring and classifier MultilayerPerceptron
     *
     * @throws Exception
     */
    @Test
    public void testSelectedRecordingsWithMultilayerPerceptron() throws Exception {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(null, null, "index");
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle"));
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle+ring"));
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
        double pctCorrect = classifyAllRecordings(trainList, gestures, new MultilayerPerceptron(), 5, 18);
        writer.println("testSelectedRecordingsWithMultilayerPerceptron pctCorrect: " + pctCorrect);
        System.out.println("testSelectedRecordingsWithMultilayerPerceptron pctCorrect: " + pctCorrect); //87.5%
    }

    /**
     * Tests recordings with the gestures index,index+middle, index+middle+ring and classifier Logistic
     *
     * @throws Exception
     */
    @Test
    public void testSelectedRecordingsWithLogistic() throws Exception {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(null, null, "index");
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle"));
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle+ring"));
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
        double pctCorrect = classifyAllRecordings(trainList, gestures, new Logistic(), 5, 18);
        writer.println("testSelectedRecordingsWithLogistic pctCorrect: " + pctCorrect);
        System.out.println("testSelectedRecordingsWithLogistic pctCorrect: " + pctCorrect); //81.25%
    }

    /**
     * Tests recordings with the gestures index,index+middle, index+middle+ring and classifier NaiveBayes
     *
     * @throws Exception
     */
    @Test
    public void testSelectedRecordingsWithNaiveBayes() throws Exception {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(null, null, "index");
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle"));
        trainList.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle+ring"));
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
        double pctCorrect = classifyAllRecordings(trainList, gestures, new NaiveBayes(), 5, 18);
        writer.println("testSelectedRecordingsWithNaiveBayes pctCorrect: " + pctCorrect);
        System.out.println("testSelectedRecordingsWithNaiveBayes pctCorrect: " + pctCorrect); //81.25%
    }

    /**
     * Tests all recordings with all gestures and filter values from 2 to 20 with Logistic
     *
     * @throws Exception
     */
    @Ignore("takes to long...")
    @Test
    public void testAllRecordingsWithDifferentFilterValuesWithLogistic() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double bestPct = 0;
        Pair<Integer, Integer> bestFilterValues = null;
        for (int j = 2; j < 20; j++) {
            for (int k = 2; k < 20; k++) {
                double temp = classifyAllRecordings(trainList, gestures, new Logistic(), j, k);
                if (temp >= bestPct) {
                    bestPct = temp;
                    bestFilterValues = Pair.of(j, k);
                }

            }
        }
        System.out.println("testAllRecordingsWithDifferentFilterValuesWithLogistic bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
        writer.println("testAllRecordingsWithDifferentFilterValuesWithLogistic bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
    }

    /**
     * Tests all recordings with all gestures and filter values from 2 to 20 with NaiveBayes
     *
     * @throws Exception
     */
    @Ignore("takes to long...")
    @Test
    public void testAllRecordingsWithDifferentFilterValuesWithNaiveBayes() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double bestPct = 0;
        Pair<Integer, Integer> bestFilterValues = null;
        for (int j = 2; j < 20; j++) {
            for (int k = 2; k < 20; k++) {
                double temp = classifyAllRecordings(trainList, gestures, new NaiveBayes(), j, k);
                if (temp >= bestPct) {
                    bestPct = temp;
                    bestFilterValues = Pair.of(j, k);
                }

            }
        }
        System.out.println("testAllRecordingsWithDifferentFilterValuesWithNaiveBayes bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
        writer.println("testAllRecordingsWithDifferentFilterValuesWithNaiveBayes bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
    }

    /**
     * Tests all recordings with all gestures and filter values from 2 to 20 with MultilayerPerceptron
     *
     * @throws Exception
     */
    @Ignore("takes to long...")
    @Test
    public void testAllRecordingsWithDifferentFilterValuesWithMultilayerPerceptron() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double bestPct = 0;
        Pair<Integer, Integer> bestFilterValues = null;
        for (int j = 2; j < 20; j++) {
            for (int k = 2; k < 20; k++) {
                double temp = classifyAllRecordings(trainList, gestures, new MultilayerPerceptron(), j, k);
                if (temp >= bestPct) {
                    bestPct = temp;
                    bestFilterValues = Pair.of(j, k);
                }

            }
        }
        System.out.println("testAllRecordingsWithDifferentFilterValuesWithMultilayerPerceptron bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
        writer.println("testAllRecordingsWithDifferentFilterValuesWithMultilayerPerceptron bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
    }

    /**
     * Tests all recordings with all gestures and filter values from 2 to 20 with J48
     *
     * @throws Exception
     */
    @Ignore("takes to long...")
    @Test
    public void testAllRecordingsWithDifferentFilterValuesWithJ48() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double bestPct = 0;
        Pair<Integer, Integer> bestFilterValues = null;
        for (int j = 2; j < 20; j++) {
            for (int k = 2; k < 20; k++) {
                double temp = classifyAllRecordings(trainList, gestures, new J48(), j, k);
                if (temp >= bestPct) {
                    bestPct = temp;
                    bestFilterValues = Pair.of(j, k);
                }

            }
        }
        System.out.println("testAllRecordingsWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
        writer.println("testAllRecordingsWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
    }

    public double classifyAllRecordings(List<Recording> trainList, List<Gesture> gestures, Classifier classifier, int labelFilter, int averageFilter) throws Exception {

        int correctCounter = 0;
        for (int i = 0; i < trainList.size(); i++) {
            Recording correctRecording = trainList.remove(0);
            //actualClasses.add(correctRecording.getGesture().getName());
            String predicted = classifyOnce(trainList, correctRecording, gestures, classifier, labelFilter, averageFilter);
            //System.out.println("correct?: " + predicted.equals(correctRecording.getGesture().getName()));
            if (predicted.equals(correctRecording.getGesture().getName()))
                correctCounter++;
            trainList.add(i, correctRecording);
        }
        return (double) correctCounter / trainList.size() * 100;
    }

    @Test
    public void simpleTest() throws Exception {
        //List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture("Peter", null, "index");
        List<Recording> trainList = recordingService.findAll();

        List<Gesture> gestures = gestureService.findAll();

        Recording correctRecording = trainList.remove(0);
        //actualClasses.add(correctRecording.getGesture().getName());
        String predicted = classifyOnce(trainList, correctRecording, gestures, new MultilayerPerceptron(), 7, 6);
        if (predicted.equals(correctRecording.getGesture().getName()))
            System.out.println("correct");
        System.out.println("predicted: " + predicted);
        System.out.println("actual: " + correctRecording.getGesture().getName());


    }


    private Integer[][] classify(String subjectName, String tag, String gestureName, Classifier cls, int averageFilterMin, int averageFilterMax, int labelFilterMin, int labelFilterMax) throws Exception {

        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(subjectName, tag, gestureName);
        //List<Recording> trainList = recordingService.findAll();
        //List<Recording> trainList = recordingService.findBySubjectNamesAndTagsAndGestures(subjectNames, tags, gestureNames);
        List<String> actualClasses = new ArrayList<>();
        Integer[][] counterArray = new Integer[labelFilterMax][averageFilterMax];
        List<Gesture> gestures = gestureService.findAll();
        //List<Gesture> gestures = gestureService.findAll().stream().limit(2).collect(Collectors.toList());
        for (int j = 0; j < labelFilterMax; j++) {
            for (int k = 0; k < averageFilterMax; k++) {
                counterArray[j][k] = 0;
            }
        }
        for (int i = 0; i < trainList.size(); i++) {
            Recording correctRecording = trainList.remove(i);
            actualClasses.add(correctRecording.getGesture().getName());
            for (int j = labelFilterMin; j < labelFilterMax; j++) {
                for (int k = averageFilterMin; k < averageFilterMax; k++) {
                    String predicted = classifyOnce(trainList, correctRecording, gestures, cls, j, k);
                    System.out.println("correct: " + predicted.equals(actualClasses.get(i)) + " predicted: " + predicted + " actual: " + correctRecording.getGesture().getName());
                    if (predicted.equals(actualClasses.get(i))) {
                        System.out.println("correct i: " + i + " Label: " + j + " Average: " + k + " classifier: " + cls.getClass().getSimpleName());
                        counterArray[j][k]++;
                    }
                }
            }
            trainList.add(i, correctRecording);
        }
        return counterArray;
    }

    private String classifyOnce(List<Recording> trainList, Recording correctRecording, List<Gesture> gestures, Classifier cls, int labelFilter, int averageFilter) throws Exception {

        WekaTool wekaTool = new WekaTool.Builder()
                .setAverageFilter(new AverageFilter(averageFilter))
                .setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8))
                .setLabelFilter(new LabelFilter(labelFilter)).build();

        Instances train = wekaTool.convertToTrainSet(trainList, gestures);
        //System.out.println(train);
        Instances test = wekaTool.convertToTestSet(correctRecording, gestures);
        //System.out.println(test);

        return wekaTool.evaluate(train, test, cls, gestures);
    }

    private void printArray(Integer[][] data, String key, String fileName, int averageFilterMin, int averageFilterMax, int labelFilterMin, int labelFilterMax) {
        System.out.println(key);
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileOutputStream(new File(fileName), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int j = labelFilterMin; j < labelFilterMax; j++) {
            for (int k = averageFilterMin; k < averageFilterMax; k++) {
                writer.append(data[j][k].toString() + ", ");
                System.out.printf("%d, ", data[j][k]);
            }
            writer.println();
            System.out.println();
        }
        writer.close();
    }

    private void printBestFilterCombo(Integer[][] data, String key, String fileName, int averageFilterMin, int averageFilterMax, int labelFilterMin, int labelFilterMax) {
        System.out.println(key);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(new File(fileName), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bestResult = 0;
        Pair<Integer, Integer> filerValues = null;
        for (int j = labelFilterMin; j < labelFilterMax; j++) {
            for (int k = averageFilterMin; k < averageFilterMax; k++) {
                if (data[j][k] >= bestResult) {
                    bestResult = data[j][k];
                    filerValues = Pair.of(j, k);
                }
            }

        }
        writer.println();
        writer.append("Best Filter-Combo(label,average) = result: " + filerValues + " = " + bestResult);
        writer.println();
        writer.append("pctCorrect: " + ((double) bestResult / (double) (averageFilterMax - averageFilterMin) * 100));
        writer.close();
        System.out.println("Best Filter-Combo(label,average) = result: " + filerValues + " = " + bestResult);
        System.out.println("pctCorrect: " + ((double) bestResult / (double) (averageFilterMax - averageFilterMin) * 100));
    }
}
