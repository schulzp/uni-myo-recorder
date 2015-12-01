package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.AbstractIdleService;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * EMG data recorder.
 */
public class EmgDataRecorder extends AbstractIdleService {

    private final List<EmgDataRecord> records = new LinkedList<>();

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
    protected void shutDown() throws Exception {
        hub.removeListener(listener);
        myo.setStreamEmg(StreamEmgType.STREAM_EMG_DISABLED);
    }

    private static class EmgDataRecord {

        private final long timestamp;

        private final byte[] data;

        private EmgDataRecord(long timestamp, byte[] data) {
            this.timestamp = timestamp;
            this.data = data;
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
