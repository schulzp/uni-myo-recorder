package edu.crimpbit;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiConsumer;

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

    public void save(String fileName, BiConsumer<Integer, Integer> progressListener) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName))) {
            String[] nextLine = {"timestamp", "emg0", "emg1", "emg2", "emg3", "emg4", "emg5", "emg6", "emg7"};
            long firstTimestamp = -1;
            csvWriter.writeNext(nextLine);
            for (int recordIndex = 0; recordIndex < recorder.getRecords().size(); ++recordIndex) {
                EmgDataRecorder.EmgDataRecord record = recorder.getRecords().get(recordIndex);
                if (firstTimestamp == -1) {
                    firstTimestamp = record.getTimestamp();
                }
                nextLine[0] = Long.toString(record.getTimestamp() - firstTimestamp);
                for (int emgIndex = 0; emgIndex < 8; ++emgIndex) {
                    nextLine[emgIndex + 1] = Byte.toString(record.getData()[emgIndex]);
                }
                csvWriter.writeNext(nextLine);
                progressListener.accept(recordIndex + 1, recorder.getRecords().size());
            }
        }
    }

}
