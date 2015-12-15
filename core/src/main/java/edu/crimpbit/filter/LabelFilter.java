package edu.crimpbit.filter;

import edu.crimpbit.Recording;

import java.util.List;

/**
 * Created by dtm on 14-Dec-15.
 */
public class LabelFilter implements SensorFilter {

    private static final int STD_VALUE = 5;
    private static final int EMG_VALUES = 255;
    private static final int MAX_EMG = 125;
    private static final int MIN_EMG = 130;
    private int labelSteps;


    @Override
    public int[][] filter(List<Recording.EmgRecord> emgRecords) {
        if (labelSteps <= 0) {
            labelSteps = STD_VALUE;
        }
        int labelInteval = EMG_VALUES / labelSteps;
        int[][] filteredRecords = new int[emgRecords.size()][EMG_SENSORS];
        for (int i = 0; i < emgRecords.size(); i++) {
            filteredRecords[i] = label(emgRecords.get(i).getData(), labelInteval);
        }
        return filteredRecords;
    }

    @Override
    public void setValue(int value) {
        this.labelSteps = value;
    }

    private int[] label(byte[] emgRecord, int labelInteval) {
        int[] filteredRecords = new int[emgRecord.length];
        for (int i = 0; i < emgRecord.length; i++) {
            filteredRecords[i] = getLabel(emgRecord[i], labelInteval);
        }

        return filteredRecords;
    }

    private int getLabel(byte singleEmgRecord, int labelSteps) {
        return (singleEmgRecord + MIN_EMG + 1) / labelSteps;
    }
}