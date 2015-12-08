package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.AbstractIdleService;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A recorder for recording data observable using a {@link DeviceListener}.
 */
public class Recorder extends AbstractIdleService {

    private final Recording recording;

    private final ObservableList emgRecords;

    private final DeviceListener listener = new AbstractDeviceListener() {

        @Override
        public void onEmgData(Myo myo, long timestamp, byte[] emg) {
            emgRecords.add(new Recording.EmgRecord(timestamp, emg));
        }

    };

    private final Hub hub;
    private final Myo myo;

    public Recorder(Hub hub, Myo myo, Recording recording) {
        this.hub = hub;
        this.myo = myo;
        this.recording = recording;
        this.emgRecords = FXCollections.observableList(recording.getEmgRecords());
    }

    public Recording getRecording() {
        return recording;
    }

    public ObservableList<Recording.EmgRecord> getRecords() {
        return emgRecords;
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
