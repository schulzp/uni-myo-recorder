package edu.crimpbit.filter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dtm on 14-Dec-15.
 */
public class LabelFilter implements Function<Stream<Byte>, Stream<Integer>>, Cloneable {

    private static final int EMG_VALUES = 255;
    private static final int MAX_EMG = 127;
    private static final int MIN_EMG = -128;
    private int numOfLabels;
    private double labelInterval;

    public LabelFilter(int numOfLabels) {
        this.numOfLabels = numOfLabels;
    }

    @Override
    public Stream<Integer> apply(Stream<Byte> byteStream) {
        if (numOfLabels <= 0)
            throw new IllegalArgumentException();
        labelInterval = EMG_VALUES / (double) numOfLabels;
        List<Integer> res = byteStream.map(this::getLabel).collect(Collectors.toList());
        int min = res.stream().min(Integer::compareTo).get();
        return res.stream().map(emgValue -> normalize(emgValue, min));
    }

    private int getLabel(byte emgValue) {
        for (int i = 1; i <= numOfLabels; i++) {
            if (emgValue <= (MIN_EMG + (labelInterval * i)))
                return i;
        }
        return 0;
    }

    private int normalize(int emgValue, int normalizer) {
        return (emgValue - normalizer) + 1;
    }

}