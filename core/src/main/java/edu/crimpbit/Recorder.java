package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.AbstractIdleService;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

/**
 * A recorder for recording data observable using a {@link DeviceListener}.
 */
public class Recorder extends AbstractIdleService {

    private final Recording recording;

    private final DeviceListener listener = new AbstractDeviceListener() {

        int count = 0;

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {
            if (count++ % 2 == 0) {
                recording.getEmgRecords().add(new Recording.EmgRecord(timestamp, emg));
            }
        }

    };

    private final Hub hub;
    private final Myo myo;

    public Recorder(Hub hub, Myo myo, Recording recording) {
        this.hub = hub;
        this.myo = myo;
        this.recording = recording;
    }

    public Recording getRecording() {
        return recording;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("recording", recording).toString();
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

}
