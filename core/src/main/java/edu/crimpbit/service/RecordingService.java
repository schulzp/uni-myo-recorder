package edu.crimpbit.service;

import edu.crimpbit.Device;
import edu.crimpbit.Recorder;
import edu.crimpbit.Recording;
import edu.crimpbit.repository.EMGDataRepository;
import edu.crimpbit.repository.RecordingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.config.ParsingUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service providing means to record data.
 */
@Service
public class RecordingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingService.class);

    private final ConnectorService connectorService;
    private final RecordingRepository recordingRepository;
    private final EMGDataRepository emgDataRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private Recorder recorder;

    @Autowired
    public RecordingService(ConnectorService connectorService, RecordingRepository recordingRepository, EMGDataRepository emgDataRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.connectorService = connectorService;
        this.recordingRepository = recordingRepository;
        this.emgDataRepository = emgDataRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Recorder createRecorder(Device device) {
        if (recorder != null && recorder.isRunning()) {
            throw new IllegalStateException("There's a recorder still running: " + recorder);
        }
        Recording recording = new Recording(device.getArm(), device.getXDirection(), device.getRotation());
        recorder = new Recorder(device.getHub(), device.getMyo(), recording);
        return recorder;
    }

    public void save(Recording recording) {
        try {
            emgDataRepository.save(recording.getEmgData());
            recordingRepository.save(recording);
        } catch (Exception e) {
            LOGGER.error("Failed to save recording.", e);
            throw e;
        }
        applicationEventPublisher.publishEvent(recording);
    }

    public List<Recording> findAll() {
        try {
            return recordingRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Failed to load recordings.", e);
            throw e;
        }
    }

    public List<Recording> findBySubjectIdAndGestureAndTag(String id, String gesture, String tag) {
        try {
            return recordingRepository.findBySubjectIdAndGestureAndTag(id, gesture, tag);
        } catch (Exception e) {
            LOGGER.error("Failed to load recordings.", e);
            throw e;
        }
    }

    public List<Recording> findBySubjectIdAndGesture(String id, String gesture) {
        try {
            return recordingRepository.findBySubjectIdAndGesture(id, gesture);
        } catch (Exception e) {
            LOGGER.error("Failed to load recordings.", e);
            throw e;
        }
    }

    public List<Recording> findBySubjectIdAndTag(String id, String tag) {
        try {
            return recordingRepository.findBySubjectIdAndTag(id, tag);
        } catch (Exception e) {
            LOGGER.error("Failed to load recordings.", e);
            throw e;
        }
    }

    public List<Recording> findBySubjectNameAndTagAndGesture(String name, String tag, String gesture) {
        try {
            return recordingRepository.findBySubjectNameAndTagAndGesture(name, tag, gesture);
        } catch (Exception e) {
            LOGGER.error("Failed to load recordings.", e);
            throw e;
        }
    }

//    public List<Recording> findBySubjectNamesAndTagsAndGestures(List<String> names, List<String> tags, List<String> gestures) {
//        try {
//            return recordingRepository.findBySubjectNamesAndTagsAndGestures(names, tags, gestures);
//        } catch (Exception e) {
//            LOGGER.error("Failed to load recordings.", e);
//            throw e;
//        }
//    }
}
