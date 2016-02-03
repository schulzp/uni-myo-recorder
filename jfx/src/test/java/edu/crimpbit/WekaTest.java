package edu.crimpbit;

import com.opencsv.CSVWriter;
import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.RecordingService;
import edu.crimpbit.service.SubjectService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private SubjectService subjectService;

    private static PrintWriter writer = null;

    private static List<Classifier> classifiers = new ArrayList();

    @BeforeClass
    public static void oneTimeSetUp() {
        classifiers.add(new J48());
        classifiers.add(new NaiveBayes());
        classifiers.add(new LibSVM());
        classifiers.add(new Logistic());
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
    @Ignore
    @Test
    public void testAllRecordingsWithLogistic() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new Logistic(), 19, 12);
        writer.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect); //46.875%

    }

    /**
     * Tests all recordings with all gestures and classifier LibSVM
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testAllRecordingsWithLibSVM() throws Exception {
        List<Recording> trainList = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double pctCorrect = classifyAllRecordings(trainList, gestures, new LibSVM(), 19, 12);
        writer.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect);
        System.out.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect); //46.875%

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

    @Test
    public void crossValidateAllWithDifferentFilterValuesWithJ48() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        double worstPct = 100;
        double bestPct = 0;
        Pair<Integer, Integer> bestFilterValues = null;
        Pair<Integer, Integer> worstFilterValues = null;
        for (int j = 0; j < 20; j++) {
            for (int k = 0; k < 30; k++) {
                double temp = crossValidateOnce(new J48(), recordings, gestures, j, k).pctCorrect();
                if (temp >= bestPct) {
                    bestPct = temp;
                    bestFilterValues = Pair.of(j, k);
                }
                if (temp <= worstPct) {
                    worstPct = temp;
                    worstFilterValues = Pair.of(j, k);
                }
            }
        }
        System.out.println("crossValidateAllWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
        writer.println("crossValidateAllWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");

        System.out.println("crossValidateAllWithDifferentFilterValuesWithJ48 worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
        writer.println("crossValidateAllWithDifferentFilterValuesWithJ48 worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
    }

//    @Test
//    public void crossValidateAllWithDifferentFilterValuesWithLibSVM() throws Exception {
//        List<Recording> recordings = recordingService.findAll();
//        List<Gesture> gestures = gestureService.findAll();
//        double worstPct = 100;
//        double bestPct = 0;
//        Pair<Integer, Integer> bestFilterValues = null;
//        Pair<Integer, Integer> worstFilterValues = null;
//        for (int j = 0; j < 20; j++) {
//            for (int k = 0; k < 30; k++) {
//                double temp = crossValidateOnce(new LibSVM(), recordings, gestures, j, k).pctCorrect();
//                if (temp >= bestPct) {
//                    bestPct = temp;
//                    bestFilterValues = Pair.of(j, k);
//                }
//                if (temp <= worstPct) {
//                    worstPct = temp;
//                    worstFilterValues = Pair.of(j, k);
//                }
//                System.out.println("i: " + j);
//            }
//        }
//        System.out.println("crossValidateAllWithDifferentFilterValuesWithLibSVM bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
//        writer.println("crossValidateAllWithDifferentFilterValuesWithLibSVM bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
//
//        System.out.println("crossValidateAllWithDifferentFilterValuesWithLibSVM worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
//        writer.println("crossValidateAllWithDifferentFilterValuesWithLibSVM worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
//    }

    //TODO
    @Test
    public void crossValidateAllWithDifferentFilterValuesWithLogistic() throws Exception {

    }

    //TODO
    @Test
    public void crossValidateAllWithDifferentFilterValuesWithNaiveBayes() throws Exception {

    }

    /**
     * cross validates all recordings with j48 and prints confusion matrix into csv
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithJ48() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        Evaluation evaluation = crossValidateOnce(new J48(), recordings, gestures, 5, 18);
        printConfusionMatrix("crossValidateAllRecordingsWithJ48", evaluation.confusionMatrix(), gestures);
    }


    /**
     * cross validates all recordings with j48 and prints confusion matrix into csv, one for each tag
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithDifferentTagsWithJ48() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        for (String tag : gestureService.getTags()) {
            List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(null, tag, null);
            Evaluation evaluation = crossValidateOnce(new J48(), recordings, gestures, 5, 18);
            String fileName = System.currentTimeMillis() + "-" + tag + "-WithJ48";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }


    @Test
    public void crossValidateSelectedRecordingsWithJ48() throws Exception {
        List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(null, null, "index");
        recordings.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle"));
        recordings.addAll(recordingService.findBySubjectNameAndTagAndGesture(null, null, "index+middle+ring"));
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
        crossValidateOnce(new J48(), recordings, gestures, 5, 18);
    }

    /**
     * cross validates all recordings with Logistic and prints confusion matrix into csv
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithLogistic() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        Evaluation evaluation = crossValidateOnce(new Logistic(), recordings, gestures, 5, 18);
        printConfusionMatrix("crossValidateAllRecordingsWithLogistic", evaluation.confusionMatrix(), gestures);
    }


    /**
     * cross validates all recordings with Logistic and prints confusion matrix into csv, one for each tag
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithDifferentTagsWithLogistic() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        for (String tag : gestureService.getTags()) {
            List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(null, tag, null);
            Evaluation evaluation = crossValidateOnce(new Logistic(), recordings, gestures, 5, 18);
            String fileName = System.currentTimeMillis() + "-" + tag + "-WithLogistic";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }

    /**
     * cross validates all recordings with NaiveBayes and prints confusion matrix into csv
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithNaiveBayes() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        List<Gesture> gestures = gestureService.findAll();
        Evaluation evaluation = crossValidateOnce(new NaiveBayes(), recordings, gestures, 5, 18);
        printConfusionMatrix("crossValidateAllRecordingsWithLogistic", evaluation.confusionMatrix(), gestures);
    }


    /**
     * cross validates all recordings with NaiveBayes and prints confusion matrix into csv, one for each tag
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithDifferentTagsWithNaiveBayes() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        for (String tag : gestureService.getTags()) {
            List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(null, tag, null);
            Evaluation evaluation = crossValidateOnce(new NaiveBayes(), recordings, gestures, 5, 18);
            String fileName = System.currentTimeMillis() + "-" + tag + "-WithNaiveBayes";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }


    /**
     * cross validates all recordings with LibSVM and prints confusion matrix into csv, one for each tag
     *
     * @throws Exception
     */
    @Test
    public void crossValidateAllRecordingsWithDifferentTagsWithLibSVM() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        for (String tag : gestureService.getTags()) {
            List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(null, tag, null);
            Evaluation evaluation = crossValidateOnce(new LibSVM(), recordings, gestures, 5, 18);
            String fileName = System.currentTimeMillis() + "-" + tag + "-WithLibSVM";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }

    @Test
    public void crossValidateAllRecordingsWithDifferentSubjects() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        for (Subject subject : subjectService.findAll()) {
            List<Recording> recordings = recordingService.findBySubjectNameAndTagAndGesture(subject.getName(), null, null);
            Evaluation evaluation = crossValidateOnce(new J48(), recordings, gestures, 15, 27);
            String fileName = System.currentTimeMillis() + "-" + subject.getName() + "-WithNaiveBayes";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }


    @Test
    public void findBestFilterPairForEachClassifier() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        Map<String, String> summaryStrings = new HashMap<>();
        List<Recording> recordings = recordingService.findAll();

        for (Classifier cls : classifiers) {
            String summaryString = "";
            for (int j = 2; j < 20; j++) {
                for (int k = 2; k < 20; k++) {
                    double bestPct = 0;
                    Pair<Integer, Integer> bestFilterValues = null;
                    Evaluation evaluation = crossValidateOnce(cls, recordings, gestures, 15, 27);
                    double temp = evaluation.pctCorrect();
                    if (temp >= bestPct) {
                        bestPct = temp;
                        bestFilterValues = Pair.of(j, k);
                        summaryStrings.put("==== " + cls.getClass().getSimpleName() + " for all ====",
                                evaluation.toSummaryString(true) + "\n" +
                                        evaluation.toMatrixString("=== Confusion Matrix  ===") + "\n" +
                                        evaluation.toClassDetailsString());
                    }

                    summaryStrings.put(

                }
            }
        }
        printSummaryStrings("testOnClassifierForAllvsOneClassifierPerPerson-" + System.currentTimeMillis(), summaryStrings);
    }

    /**
     * Testing One Classifier For All vs One Classifier Per Person
     *
     * @throws Exception
     */
    //TODO: save filter values for each classifier in classifiers
    @Test
    public void testOnClassifierForAllvsOneClassifierPerPerson() throws Exception {
        List<Gesture> gestures = gestureService.findAll();
        List<Recording> recordings = recordingService.findAll();
        Map<String, String> summaryStrings = new HashMap<>();
        for (Classifier cls : classifiers) {
            Evaluation evaluation = crossValidateOnce(cls, recordings, gestures, 15, 27);
            summaryStrings.put("==== " + cls.getClass().getSimpleName() + " for all ====",
                    evaluation.toSummaryString(true) + "\n" +
                            evaluation.toMatrixString("=== Confusion Matrix  ===") + "\n" +
                            evaluation.toClassDetailsString());
        }

        for (Classifier cls : classifiers) {
            for (Subject subject : subjectService.findAll()) {
                List<Recording> recordingsBySubject = recordingService.findBySubjectNameAndTagAndGesture(subject.getName(), null, null);
                Evaluation evaluation = crossValidateOnce(cls, recordingsBySubject, gestures, 15, 27);
                summaryStrings.put("==== " + cls.getClass().getSimpleName() + " for " + subject.getName() + " ====",
                        evaluation.toSummaryString(true) + "\n" +
                                evaluation.toMatrixString("=== Confusion Matrix  ===") + "\n" +
                                evaluation.toClassDetailsString());
            }
        }
        printSummaryStrings("testOnClassifierForAllvsOneClassifierPerPerson-" + System.currentTimeMillis(), summaryStrings);

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

    private Evaluation crossValidateOnce(Classifier cls, List<Recording> recordings, List<Gesture> gestures, int labelFilter, int averageFilter) throws Exception {
        WekaTool.Builder wekaToolBulder = new WekaTool.Builder().setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8));
        if (labelFilter != 0) {
            wekaToolBulder.setLabelFilter(new LabelFilter(labelFilter));
        }
        if (averageFilter != 0) {
            wekaToolBulder.setAverageFilter(new AverageFilter(averageFilter));
        }
        WekaTool wekaTool = wekaToolBulder.build();
        Instances data = wekaTool.convertToTrainSet(recordings, gestures);
        return wekaTool.crossValidate(data, cls, gestures);
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

    private void printConfusionMatrix(String fileName, double[][] confusionMatrix, List<Gesture> gestures) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
            String[] gesturesArray = new String[gestures.size() + 1];
            gesturesArray[0] = "Class";
            for (int i = 0; i < gestures.size(); i++) {
                gesturesArray[i + 1] = gestures.get(i).getName();
            }
            writer.writeNext(gesturesArray);


            for (int i = 0; i < confusionMatrix.length; i++) {
                ArrayList<String> rowList = new ArrayList<>();
                rowList.add(gestures.get(i).getName());
                for (double elem : confusionMatrix[i]) {
                    rowList.add(String.valueOf(elem));
                }
                //String a = Arrays.toString(row);
                writer.writeNext(rowList.toArray(new String[rowList.size()]));

            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printSummary(String fileName, Evaluation evaluation) throws Exception {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(new File(fileName + ".txt"), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.println(evaluation.toSummaryString(true));
        writer.println(evaluation.toMatrixString("=== Confusion Matrix  ==="));
        writer.println(evaluation.toClassDetailsString());
        writer.close();
    }

    private void printSummaryStrings(String fileName, Map<String, String> summaryStrings) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(new File(fileName + ".txt"), true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, String> summaryString : summaryStrings.entrySet()) {
            writer.println(summaryString.getKey());
            writer.println(summaryString.getValue());
        }
        writer.close();
    }
}
