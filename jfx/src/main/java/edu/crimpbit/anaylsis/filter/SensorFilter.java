package edu.crimpbit.anaylsis.filter;

import edu.crimpbit.Recording;

import java.util.List;

/**
 * Created by dtm on 14-Dec-15.
 */
public interface SensorFilter {
    static final int EMG_SENSORS = 8;
    int[][] filter(List<Recording.EmgRecord> emgRecords);
    void setValue(int value);
}
