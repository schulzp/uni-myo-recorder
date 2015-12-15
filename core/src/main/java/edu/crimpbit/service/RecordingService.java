package edu.crimpbit.service;

import com.opencsv.CSVWriter;
import edu.crimpbit.Device;
import edu.crimpbit.Recorder;
import edu.crimpbit.Recording;
import edu.crimpbit.repository.RecordingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Service providing means to record data.
 */
@Service
public class RecordingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingService.class);

    private final ConnectorService connectorService;
    private final RecordingRepository recordingRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private Recorder recorder;

    @Autowired
    public RecordingService(ConnectorService connectorService, RecordingRepository recordingRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.connectorService = connectorService;
        this.recordingRepository = recordingRepository;
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
        recordingRepository.save(recording);
        applicationEventPublisher.publishEvent(recording);
    }

    public List<Recording> findAll() {
        return recordingRepository.findAll();
    }

    public void save(Recording recording, String fileName, BiConsumer<Integer, Integer> progressListener) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName))) {
            String[] nextLine = {"timestamp", "emg0", "emg1", "emg2", "emg3", "emg4", "emg5", "emg6", "emg7"};
            long firstTimestamp = -1;
            csvWriter.writeNext(nextLine);
            for (int recordIndex = 0; recordIndex < recording.getEmgRecords().size(); ++recordIndex) {
                Recording.EmgRecord record = recording.getEmgRecords().get(recordIndex);
                if (firstTimestamp == -1) {
                    firstTimestamp = record.getTimestamp();
                }
                nextLine[0] = Long.toString(record.getTimestamp() - firstTimestamp);
                for (int emgIndex = 0; emgIndex < 8; ++emgIndex) {
                    nextLine[emgIndex + 1] = Byte.toString(record.getData()[emgIndex]);
                }
                csvWriter.writeNext(nextLine);
                progressListener.accept(recordIndex + 1, recording.getEmgRecords().size());
            }
        }
    }

}
