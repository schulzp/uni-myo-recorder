package edu.crimpbit;

import com.thalmic.myo.Quaternion;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * IMU data recording.
 */
public class IMUData extends RawData {

    private final List<Quaternion> data = new LinkedList<>();

    @Override
    public void add(long timestamp, Object value) {

    }
}
