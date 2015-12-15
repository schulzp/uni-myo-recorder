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
        Recording.EmgRecord emgRecord = new Recording.EmgRecord(0, new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
        int[][] result = this.filter.filter(Collections.singletonList(emgRecord));
    }

    @Test
    public void filterMultipleRecords() {
        filter.setValue(2);
        List<Recording.EmgRecord> emgRecords = Arrays.asList(
                new Recording.EmgRecord(0, new byte[]{2, 2, 2, 2, 2, 2, 2, 2}),
                new Recording.EmgRecord(0, new byte[]{0, 0, 0, 0, 0, 0, 0, 0})
        );
        int[][] result = this.filter.filter(emgRecords);
    }

}