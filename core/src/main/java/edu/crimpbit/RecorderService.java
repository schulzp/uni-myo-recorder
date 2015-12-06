package edu.crimpbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service providing means to record data.
 */
public class RecorderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecorderService.class);

    private final ConnectorService connectorService;

    private EmgDataRecorder recorder;

    public RecorderService(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public EmgDataRecorder createRecorder(Device device) {
        if (recorder != null && recorder.isRunning()) {
            throw new IllegalStateException("There is a recorder still running: " + recorder);
        }
        recorder = new EmgDataRecorder(connectorService.getHub(), device.getMyo());
        return recorder;
    }

}
