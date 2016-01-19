package edu.crimpbit.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Created by dtm on 14-Dec-15.
 */
public class AverageFilter implements Function<Stream<Byte>, Stream<Double>> {

    private final int chunk;

    public AverageFilter(int chunk) {
        this.chunk = chunk;
    }

    @Override
    public Stream<Double> apply(Stream<Byte> byteStream) {
        if (chunk == 0)
            throw new IllegalArgumentException();
        return toChunkedStream(byteStream.collect(Collectors.toList()))
                .map(doubleStream -> doubleStream.average().getAsDouble());
    }

    private Stream<DoubleStream> toChunkedStream(List<Byte> bytes) {
        List<DoubleStream> result = new ArrayList<>();
        for (int begin = 0, end, size; begin < bytes.size(); begin = end) {
            end = Math.min(begin + chunk, bytes.size());
            result.add(bytes.subList(begin, end).stream().mapToDouble(Byte::doubleValue));
        }
        return result.stream();
    }
}