package edu.crimpbit;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * Created by peter on 03/02/16.
 */
public class DataSplitterTest {

    DataSplitter<Integer> splitter = new DataSplitter<>();

    @Test
    public void applyTest() {
        List<Integer> result = splitter.apply(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), DataSplitter.DataType.TEST);
        assertThat(result, IsIterableContainingInOrder.contains(8, 9));
    }

    @Test
    public void applyTraining() {
        List<Integer> result = splitter.apply(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), DataSplitter.DataType.TRAINING);
        assertThat(result, IsIterableContainingInOrder.contains(0, 1, 2, 3, 4, 5, 6, 7));
    }

}