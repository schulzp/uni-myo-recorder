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
import weka.core.Instances;

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

//    @Test
//    public void test3() throws Exception {
//        List<Recording> recordings = recordingService.findAll().stream().map(recording -> {
//            return new NormalizeFilter().apply(recording);
//        }).collect(Collectors.toList());
//        System.out.println(Converter.convert(recordings));
//    }
}
