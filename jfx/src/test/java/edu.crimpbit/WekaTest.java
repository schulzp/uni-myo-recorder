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

        trainList.remove(0);
        trainList.remove(0);
        for (Recording testRecordingA: trainList) {
            Instances train = WekaTool.convert(trainList);
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

            String prediction = WekaTool.testAllClasses(train, testList);
            System.out.println("Predicted Class: " + prediction + " Actual Class : " + actualClass);
            System.out.println("Prediction correct: " + prediction.equals(actualClass));
        }
    }
}
