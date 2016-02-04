package edu.crimpbit;

import com.opencsv.CSVWriter;
import com.thalmic.myo.enums.Arm;
import edu.crimpbit.config.CoreConfiguration;
import edu.crimpbit.filter.AverageFilter;
import edu.crimpbit.filter.EnvelopeFollowerFilter;
import edu.crimpbit.filter.LabelFilter;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.RecordingService;
import edu.crimpbit.service.SubjectService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;
import org.junit.rules.TestName;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Dario on 16.01.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfiguration.class})
@Transactional
@Rollback
public class WekaTest {

    private static final List<Supplier<Classifier>> CLASSIFIERS = Arrays.asList(J48::new, NaiveBayes::new, LibSVM::new, Logistic::new);

    private static final EnumSet<Arm> ARMS = EnumSet.of(Arm.ARM_LEFT, Arm.ARM_RIGHT);

    @Rule
    public TestName testName = new TestName();

    @Autowired
    RecordingService recordingService;

    @Autowired
    private GestureService gestureService;

    @Autowired
    private SubjectService subjectService;

    private CSVWriter summaryWriter = null;

    private PrintWriter detailWriter = null;

    @Before
    public void beforeTest() throws IOException {
        String testFileName = testName.getMethodName() + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replace(":", ".");
        summaryWriter = new CSVWriter(new FileWriter(testFileName + ".csv", false), ',');
        detailWriter = new PrintWriter(new FileWriter(testFileName + ".txt", false));
        printResultCSVHeader();
    }

    @After
    public void afterTest() throws IOException {
        summaryWriter.close();
        detailWriter.close();
    }

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
        detailWriter.println("testAllRecordingsWithJ48 pctCorrect: " + pctCorrect);
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
        detailWriter.println("testAllRecordingsWithNaiveBayes pctCorrect: " + pctCorrect);
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
        detailWriter.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect);
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
        detailWriter.println("testAllRecordingsWithLogistic pctCorrect: " + pctCorrect);
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
        detailWriter.println("testSelectedRecordingsWithJ48 pctCorrect: " + pctCorrect);
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
        detailWriter.println("testSelectedRecordingsWithLogistic pctCorrect: " + pctCorrect);
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
        detailWriter.println("testSelectedRecordingsWithNaiveBayes pctCorrect: " + pctCorrect);
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
        detailWriter.println("testAllRecordingsWithDifferentFilterValuesWithLogistic bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
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
        detailWriter.println("testAllRecordingsWithDifferentFilterValuesWithNaiveBayes bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
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
        detailWriter.println("testAllRecordingsWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");
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
                double temp = crossValidate(new J48(), recordings, j, k).pctCorrect();
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
        detailWriter.println("crossValidateAllWithDifferentFilterValuesWithJ48 bestFilterValues: " + bestFilterValues + " = " + bestPct + "%");

        System.out.println("crossValidateAllWithDifferentFilterValuesWithJ48 worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
        detailWriter.println("crossValidateAllWithDifferentFilterValuesWithJ48 worstFilterValues: " + worstFilterValues + " = " + worstPct + "%");
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
        Evaluation evaluation = crossValidate(new J48(), recordings, 5, 18);
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
            Evaluation evaluation = crossValidate(new J48(), recordings, 5, 18);
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
        crossValidate(new J48(), recordings, 5, 18);
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
        Evaluation evaluation = crossValidate(new Logistic(), recordings, 5, 18);
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
            Evaluation evaluation = crossValidate(new Logistic(), recordings, 5, 18);
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
        Evaluation evaluation = crossValidate(new NaiveBayes(), recordings, 5, 18);
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
            Evaluation evaluation = crossValidate(new NaiveBayes(), recordings, 5, 18);
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
            Evaluation evaluation = crossValidate(new LibSVM(), recordings, 5, 18);
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
            Evaluation evaluation = crossValidate(new J48(), recordings, 15, 27);
            String fileName = System.currentTimeMillis() + "-" + subject.getName() + "-WithNaiveBayes";
            printConfusionMatrix(fileName + "-ConfusionMatrix", evaluation.confusionMatrix(), gestures);
            printSummary(fileName + "-Summary", evaluation);
        }
    }


    @Test
    public void findBestFilterPairForEachClassifier() throws Exception {
        List<Recording> recordings = recordingService.findAll();
        int progressCounter = 0;
        for (Supplier cls : CLASSIFIERS) {
            Classifier classifier = (Classifier) cls.get();
            for (int j = 0; j < 20; j++) {
                for (int k = 0; k < 30; k++) {
                    progressCounter++;
                    System.out.println(progressCounter + " of " + (CLASSIFIERS.size() * 20 * 30));
                    evaluateClassifier("Test-Per-Filter-Value", null, null, null, classifier, recordings, detailWriter, j, k);
                }
            }
        }
    }

    @Test
    public void testOnClassifierForAllSubjectsVsOneClassifierPerSubject() throws Exception {
        for (Supplier classifierSupplier : CLASSIFIERS) {
            for (String tag : gestureService.getTags()) {
                for (Arm arm : ARMS) {
                    Classifier classifier = (Classifier) classifierSupplier.get();

                    List<Recording> recordings = recordingService.findBySubjectNameAndArmAndTagAndGesture(null, arm, tag, null);

                    if (!recordings.isEmpty()) {
                        evaluateClassifier("Test-Per-Person", null, arm, null, classifier, recordings, detailWriter, 15, 17);
                    }
                }
            }
        }

        evaluateClassifiersPerVariable("Test-Per-Variable");
    }

    @Test
    public void testOneClassifierForAllPullDirectionsVsOneClassifierPerPullDirection() throws Exception {
        for (Supplier<Classifier> classifierSupplier : CLASSIFIERS) {
            for (Subject subject : subjectService.findAll()) {
                for (Arm arm : ARMS) {
                    Classifier classifier = classifierSupplier.get();
                    List<Recording> recordings = recordingService.findBySubjectNameAndArmAndTagAndGesture(subject.getId(), arm, null, null);


                    if (!recordings.isEmpty()) {
                        evaluateClassifier("Test-Per-Direction", subject, arm, null, classifier, recordings, detailWriter, 15, 17);
                    }
                }
            }
        }

        evaluateClassifiersPerVariable("Test-Per-Variable");
    }

    @Test
    public void testOneClassifierForBothArmsVsOneClassifierPerArm() throws Exception {
        for (Supplier<Classifier> classifierSupplier : CLASSIFIERS) {
            for (Subject subject : subjectService.findAll()) {
                for (String tag : gestureService.getTags()) {
                    Classifier classifier = classifierSupplier.get();
                    List<Recording> recordings = recordingService.findBySubjectNameAndArmAndTagAndGesture(subject.getId(), null, tag, null);

                    if (!recordings.isEmpty()) {
                        evaluateClassifier("Test-Per-Arm", subject, null, tag, classifier, recordings, detailWriter, 15, 17);
                    }
                }
            }
        }

        evaluateClassifiersPerVariable("Test-Per-Variable");
    }

    private void evaluateClassifiersPerVariable(String testName) throws Exception {
        for (Supplier<Classifier> classifierSupplier : CLASSIFIERS) {
            for (Subject subject : subjectService.findAll()) {
                for (String tag : gestureService.getTags()) {
                    for (Arm arm : ARMS) {
                        Classifier classifier = classifierSupplier.get();

                        List<Recording> recordings = recordingService.findBySubjectNameAndArmAndTagAndGesture(subject.getId(), arm, tag, null);

                        if (!recordings.isEmpty()) {
                            evaluateClassifier(testName, subject, arm, null, classifier, recordings, detailWriter, 15, 17);
                        }
                    }
                }
            }
        }
    }


    private void evaluateClassifier(String testName, Subject subject, Arm arm, String tag, Classifier classifier, List<Recording> recordings, PrintWriter writer, int labelFilter, int averageFilter) throws Exception {
        WekaTool wekaTool = createWekaTool(labelFilter, averageFilter);

        InstancesSupplier trainingSetSupplier = wekaTool.convertToTrainSet(recordings);

        Instances trainingSet = trainingSetSupplier.getNormalized();
        classifier.buildClassifier(trainingSet);

        Evaluation crossValidation = WekaTool.crossValidate(trainingSet, classifier);

        writeSummary(subject, arm, tag, classifier, crossValidation, writer);

        InstancesSupplier testSetSupplier = wekaTool.convertToTestSet(recordings);
        Instances testSet = testSetSupplier.get();
        trainingSetSupplier.getNormalizer().accept(testSet);
        Evaluation testEvaluation = WekaTool.evaluate(testSet, classifier);

        writeSummary(subject, arm, tag, classifier, testEvaluation, writer);

        printResultCSV(testName, subject, tag, arm, labelFilter, averageFilter, classifier, crossValidation, testEvaluation);
    }

    private Evaluation crossValidate(Classifier cls, List<Recording> recordings, int labelFilter, int averageFilter) throws Exception {
        InstancesSupplier instancesSupplier = createWekaTool(labelFilter, averageFilter).convertToTrainSet(recordings);
        Instances instances = instancesSupplier.get();
        instancesSupplier.getNormalizer().accept(instances);
        //cls.buildClassifier(instances);
        return WekaTool.crossValidate(instances, cls);
    }

    private WekaTool createWekaTool(int labelFilter, int averageFilter) {
        WekaTool.Builder builder = new WekaTool.Builder().setEnvelopeFollowerFilter(new EnvelopeFollowerFilter(0.3, 0.8));

        if (labelFilter != 0) {
            builder.setLabelFilter(new LabelFilter(labelFilter));
        }

        if (averageFilter != 0) {
            builder.setAverageFilter(new AverageFilter(averageFilter));
        }

        builder.setGestures(gestureService.findAll());

        return builder.build();
    }

    private void writeSummary(Subject subject, Arm arm, String tag, Classifier classifier, Evaluation evaluation, PrintWriter writer) throws Exception {
        writer.write("==== " + classifier.getClass().getSimpleName() + " Subject: " + identifySubject(subject) + " Arm: " + identifyArm(arm) + " Direction: " + identifyTag(tag) + " ====\n" +
                evaluation.toSummaryString(true) + "\n" +
                evaluation.toMatrixString("=== Confusion Matrix  ===") + "\n" +
                evaluation.toClassDetailsString());
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

    private String classifyOnce(List<Recording> trainList, Recording correctRecording, List<Gesture> gestures, Classifier cls, int labelFilter, int averageFilter) throws Exception {
        WekaTool wekaTool = createWekaTool(labelFilter, averageFilter);

        InstancesSupplier train = wekaTool.convertToTrainSet(trainList);
        //System.out.println(train);
        InstancesSupplier test = wekaTool.convertToTestSet(Collections.singletonList(correctRecording));
        //System.out.println(test);

        return wekaTool.evaluate(train.get(), test.get(), cls, gestures);
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

    private void printResultCSVHeader() {
        summaryWriter.writeNext(new String[]{
                "Test",
                "Label Filter Value",
                "Average Filter Value",
                "Subject",
                "Arm",
                "Direction",
                "Classifier",
                "# CV Elements",
                "# CV Correct",
                "# CV Incorrect",
                "% CV Accuracy",
                "# Test Elements",
                "# Test Correct",
                "# Test Incorrect",
                "% Test Accuracy"});
    }

    private void printResultCSV(String testId, Subject subject, String tag, Arm arm, int labelFilter, int averageFilter, Classifier classifier, Evaluation crossValidation, Evaluation testEvaluation) {
        summaryWriter.writeNext(new String[]{
                testId,
                String.valueOf(labelFilter),
                String.valueOf(averageFilter),
                identifySubject(subject),
                identifyArm(arm),
                identifyTag(tag),
                classifier.getClass().getSimpleName(),
                String.valueOf(crossValidation.correct() + crossValidation.incorrect()),
                String.valueOf(crossValidation.correct()),
                String.valueOf(crossValidation.incorrect()),
                String.valueOf(crossValidation.pctCorrect()),
                String.valueOf(testEvaluation.correct() + testEvaluation.incorrect()),
                String.valueOf(testEvaluation.correct()),
                String.valueOf(testEvaluation.incorrect()),
                String.valueOf(testEvaluation.pctCorrect())
        });

    }

    private static String identifyTag(String tag) {
        return tag == null ? "*" : tag;
    }

    private static String identifyArm(Arm arm) {
        return arm == null ? "*" : arm.name();
    }

    private static String identifySubject(Subject subject) {
        return subject == null ? "*" : subject.getName();
    }

}
