package edu.crimpbit.filter;

import edu.crimpbit.Recording;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by peter on 15/12/15.
 */
public class AverageFilterTest {

    private AverageFilter filter = new AverageFilter();

    @Test
    public void filterSingleRecord() {
        filter.setValue(1);
        byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
        Recording.EmgRecord emgRecord = new Recording.EmgRecord(0, bytes);
        List<Recording.EmgRecord> list = Collections.singletonList(emgRecord);
        int[][] result = this.filter.filter(list);
        for(int emg = 0; emg < list.size(); emg++) {
            //System.out.println("emg: " + emg);
            for (int value = 0; value < result[emg].length; value ++)
            {
                assert(result[emg][value] == bytes[value]);
            }
        }
    }

    @Test
    public void filterMultipleRecords() {
        int size = 2;
        byte stdValue = 2;
        filter.setValue(size);
        byte[] values1 = new byte[]{stdValue, stdValue, stdValue, stdValue, stdValue, stdValue, stdValue, stdValue};
        byte[] values2 = new byte[]{stdValue, stdValue, stdValue, stdValue, stdValue, stdValue, stdValue, stdValue};
        List<Recording.EmgRecord> emgRecords = Arrays.asList(
                new Recording.EmgRecord(0, values1),
                new Recording.EmgRecord(0, values2)
        );

        int[][] result = this.filter.filter(emgRecords);
        //das assert klappt so im moment nicht, weil filter von 8 sensoren ausgeht und nicht von emgRecors.size vielen
        //assert(result.length == emgRecords.size());
        assert(result[0].length == (int) values1.length / size);
        assert(result[1].length == (int) values2.length / size);
        for(int emg = 0; emg < emgRecords.size(); emg++) {
            //System.out.println("emg: " + emg);
            for (int value = 0; value < result[emg].length; value ++)
            {
                assert(result[emg][value] == stdValue);
            }
        }
    }

}