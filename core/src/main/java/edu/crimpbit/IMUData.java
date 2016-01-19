package edu.crimpbit;

import com.thalmic.myo.Quaternion;

import java.util.LinkedList;
import java.util.List;

/**
 * IMU data recording.
 */
public class IMUData extends RawData {

    private final List<Quaternion> data = new LinkedList<>();

    @Override
    public void add(long timestamp, Object value) {

    }
}
