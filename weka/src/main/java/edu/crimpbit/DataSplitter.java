package edu.crimpbit;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Data splitter.
 */
public class DataSplitter<T> implements BiFunction<List<T>, DataSplitter.DataType, List<T>> {

    private static final double DEFAULT_TRAINING_TEST_RATIO = 0.8;

    public enum DataType {
        TRAINING, TEST
    }

    private final double trainingTestRatio;

    public DataSplitter() {
        this(DEFAULT_TRAINING_TEST_RATIO);
    }

    public DataSplitter(double trainingTestRatio) {
        this.trainingTestRatio = trainingTestRatio;
    }

    @Override
    public List<T> apply(List<T> data, DataType type) {
        int offset = (int) (data.size() * trainingTestRatio);
        if (type == DataType.TEST) {
            data = data.subList(offset, data.size());
        } else if (type == DataType.TRAINING) {
            data = data.subList(0, offset);
        }
        return data;
    }


}
