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
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Integer[][] classify(String subjectName, String tag, String gesture, Classifier cls, int filterMin, int filterMax) throws Exception {
        List<Recording> trainList = recordingService.findBySubjectNameAndTagAndGesture(subjectName, tag, gesture);
        List<String> actualClasses = new ArrayList<>();
        Integer[][] counterArray = new Integer[filterMax][filterMax];
        List<Gesture> gestures = gestureService.findAll().stream().limit(3).collect(Collectors.toList());
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

                    Instances train = wekaTool.convertToTrainSet(trainList, gestures);
                    List<Instances> test = wekaTool.convertToTestSet(correctRecording, gestures);
                    if (wekaTool.testAllClassesSynchronously(train, test, cls).equals(actualClasses.get(i))) {
                        System.out.println("correct i: " + i + " j: " + j + " k: " + k + " classifier: " + cls.getClass().getSimpleName());
                        counterArray[j][k]++;
                    }
                }
            }
            trainList.add(i, correctRecording);
        }
        return counterArray;
    }

    /**
     * variables: subject, tags, gestures, filter, Classifier
     *
     * Classifier Test: classifier for one subject, one tag, one gesture, different filter sets, different classifiers
     */
    @Test
    public void differentFiltersAndClassifiersTest() throws ExecutionException, InterruptedException {
        int filterMin = 2;
        int filterMax = 20;
        ExecutorService executor = Executors.newFixedThreadPool(3);
        HashMap<String, FutureTask<Integer[][]>> map = new HashMap<>();
        //Set<FutureTask<Integer[][]>> set = new HashSet<>();

        FutureTask<Integer[][]> futureTask1 = new FutureTask<>(() -> classify("Peter", null, "index", new J48(), filterMin, filterMax));
        //FutureTask<Integer[][]> futureTask2 = new FutureTask<>(() -> classify("Peter", null, "index", new Logistic(), filterMin, filterMax));
        //FutureTask<Integer[][]> futureTask3 = new FutureTask<>(() -> classify("Peter", null, "index", new NaiveBayes(), filterMin, filterMax));

        map.put("J48", futureTask1);
        executor.execute(futureTask1);
        // map.put("Logistic", futureTask2);
        // executor.execute(futureTask2);
        // map.put("NaiveBayes", futureTask3);
        //executor.execute(futureTask3);

        for (Map.Entry<String, FutureTask<Integer[][]>> stringFutureTaskEntry : map.entrySet()) {
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
     * variables: subject, tags, gestures, filter, Classifier
     *
     * for each gesture with different sets of subjects
     */
    @Test
    public void differentSetsOfSubjectsTest() {

    }

    /**
     * variables: subject, tags, gestures, filter, Classifier
     *
     * for each gesture with different tags and different sets of subjects
     */
    @Test
    public void differentTagsAndDifferentSetsOfSubjects() {

    }
}
