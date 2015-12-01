package edu.crimpbit;

import com.thalmic.myo.Myo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for recording data.
 */
public class RecordingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingService.class);

    private final ConnectorService connectorService;

    private EmgDataRecorder recorder;

    public RecordingService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public void startRecording(Myo myo) {
        recorder = new EmgDataRecorder(connectorService.getHub(), myo);
        LOGGER.info("starting {}", recorder);
        recorder.startAsync();
    }

    public void stopRecording(Myo myo) {
        LOGGER.info("stopping {}", recorder);
        recorder.stopAsync();
    }

}
