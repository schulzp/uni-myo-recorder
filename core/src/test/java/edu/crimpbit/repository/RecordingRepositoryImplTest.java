package edu.crimpbit.repository;

import edu.crimpbit.config.CoreConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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

}