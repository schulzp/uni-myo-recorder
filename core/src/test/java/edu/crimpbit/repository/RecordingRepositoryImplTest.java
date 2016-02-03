package edu.crimpbit.repository;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Recording;
import edu.crimpbit.config.CoreConfiguration;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Joerg on 25.01.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfiguration.class})
@Transactional
@Rollback
public class RecordingRepositoryImplTest {

    @Autowired
    private RecordingRepository recordingRepository;

    @Test
    public void testFindBySubjectIdAndGestureAndTag() throws Exception {
        recordingRepository.findBySubjectIdAndGestureAndTag("","index","pull-downwards");
    }

    @Test
    public void testFindBySubjectIdAndGesture() throws Exception {
        recordingRepository.findBySubjectIdAndGesture("", "index");

    }

    @Test
    public void testFindBySubjectIdAndTag() throws Exception {
        recordingRepository.findBySubjectIdAndTag("", "pull-downwards");
    }

    @Test
    public void findBySubjectIdAndArmAndTagAndGesture() {
        List<Recording> result = recordingRepository.findBySubjectIdAndArmAndTagAndGesture("56a3821703649612e376d221", Arm.ARM_LEFT, "index", "pull-gaston");
        assertThat(result.size(), Matchers.is(Matchers.greaterThan(0)));
    }

}