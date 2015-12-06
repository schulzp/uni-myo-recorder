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

    public EmgDataRecorder startRecording(Device device) {
        if (recorder != null && recorder.isRunning()) {
            throw new IllegalStateException("Recording is still running");
        }
        recorder = new EmgDataRecorder(connectorService.getHub(), device.getMyo());
        LOGGER.info("starting {}", recorder);
        recorder.startAsync();
        return recorder;
    }

}
