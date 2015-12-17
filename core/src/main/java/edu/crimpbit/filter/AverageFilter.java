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
        int[][] filteredRecords = new int[EMG_SENSORS][emgRecords.size()/size];
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
        int avgLength = emgRecord.length / size;
        int[] filteredRecords = new int[avgLength];
        int sum = 0;
        for (int i = 1; i <= emgRecord.length; i++) {
            sum += emgRecord[i-1];
            if (i % size == 0) {
                int avg = i / size;
                /*System.out.println("i mod size = 0:" + i);
                System.out.println("avg:" + avg);
                System.out.println("sum:" + sum);
                System.out.println("size:" + size);*/
                filteredRecords[avg - 1] = sum / size;
                sum = 0;
            }
        }
        return filteredRecords;
    }
}