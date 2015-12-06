package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.AbstractIdleService;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * EMG data recorder.
 */
public class EmgDataRecorder extends AbstractExecutionThreadService {

    private final ObservableList<EmgDataRecord> records = FXCollections.observableArrayList();

    private final DeviceListener listener = new AbstractDeviceListener() {

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {
            if (myo.equals(EmgDataRecorder.this.myo));
            records.add(new EmgDataRecord(timestamp, emg));
        }

    };

    private final Hub hub;
    private final Myo myo;

    public EmgDataRecorder(Hub hub, Myo myo) {
        this.hub = hub;
        this.myo = myo;
    }

    @Override
    protected void startUp() throws Exception {
        myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
        hub.addListener(listener);
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            hub.run(1000/100);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        hub.removeListener(listener);
        myo.setStreamEmg(StreamEmgType.STREAM_EMG_DISABLED);
    }

    public ObservableList<EmgDataRecord> getRecords() {
        return records;
    }

    public static class EmgDataRecord {

        private final long timestamp;

        private final byte[] data;

        private EmgDataRecord(long timestamp, byte[] data) {
            this.timestamp = timestamp;
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "EMG at " + timestamp + ": " + Arrays.toString(data);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("records", records).toString();
    }
}
