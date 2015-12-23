package edu.crimpbit.filter;

import one.util.streamex.EntryStream;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class EnvelopeFollowerFilterTest {

    @Test
    public void apply() throws Exception {
        EnvelopeFollowerFilter filter = new EnvelopeFollowerFilter(0.1, -0.1);

        RoughlyMatcherBuilder is = new RoughlyMatcherBuilder(0.1);

        EntryStream<Integer, Double> entries = EntryStream.of(Arrays.asList(1.0, -0.9, 0.8, -0.7, 0.6));
        List<Double> result = filter.apply(entries).values().collect(Collectors.toList());

        assertThat(result, contains(
                is.roughly(0.9),
                is.roughly(0.9),
                is.roughly(0.8),
                is.roughly(0.7),
                is.roughly(0.6)));
    }

    public static class RoughlyMatcherBuilder {

        private final double lowerOffset;
        private final double upperOffset;

        public RoughlyMatcherBuilder(double tolerance) {
            this(-tolerance, tolerance);
        }

        public RoughlyMatcherBuilder(double lowerOffset, double upperOffset) {
            this.lowerOffset = lowerOffset;
            this.upperOffset = upperOffset;
        }

        public Matcher<Double> roughly(double value) {
            return allOf(
                    greaterThanOrEqualTo(value + lowerOffset),
                    lessThanOrEqualTo(value + upperOffset));
        }

    }


}