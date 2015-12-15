package edu.crimpbit.filter;

import edu.crimpbit.Recording;

import java.util.List;

/**
 * Created by dtm on 14-Dec-15.
 */
public class AverageFilter implements SensorFilter {

    private int size;
    private static final int STD_VALUE = 5;


    @Override
    public int[][] filter(List<Recording.EmgRecord> emgRecords) {
        if (size <= 0) {
            size = STD_VALUE;
        }
        int[][] filteredRecords = new int[emgRecords.size() / size][EMG_SENSORS];
        int[] sums = new int[EMG_SENSORS];
        for (int i = 0; i < emgRecords.size(); i++) {
            filteredRecords[i] = average(emgRecords.get(i).getData(),size);
        }
        return filteredRecords;
    }

    @Override
    public void setValue(int value) {
        this.size = value;
    }

    private int[] average(byte[] emgRecord, int size) {
        int[] filteredRecords = new int[emgRecord.length / size];
        int sum = 0;
        for (int i = 0; i < emgRecord.length; i++) {
            sum += emgRecord[i];
            if (i % size == 0) {
                filteredRecords[i / size] = sum / size;
                sum = 0;
            }
        }
        return filteredRecords;
    }
}