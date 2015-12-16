package edu.crimpbit;

import one.util.streamex.EntryStream;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * EMG data recording
 */
@Document(collection = "emg-data")
public class EMGData {

    public static final int NUM_EMG_PADS = 8;

    @Id
    private String id;

    private final List<Long> timestamps = new LinkedList<>();
    private final List<List<Byte>> data = Collections.unmodifiableList(new ArrayList<List<Byte>>(NUM_EMG_PADS) {
        {
            for (int i = 0; i < NUM_EMG_PADS; ++i) {
                add(new LinkedList<>());
            }
        }
    });

    public synchronized void add(long timestamp, byte[] data) {
        timestamps.add(timestamp);
        for (int i = 0; i < NUM_EMG_PADS; ++i) {
            getData(i).add(data[i]);
        }
    }

    public EntryStream<Integer, List<Byte>> stream() {
        return EntryStream.of(data);
    }

    public synchronized List<Byte> getData(int index) {
        return data.get(index);
    }

    public synchronized int size() {
        return timestamps.size();
    }

    public String getId() {
        return id;
    }

}
