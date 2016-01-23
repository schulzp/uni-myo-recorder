package edu.crimpbit.filter;

import com.google.common.base.MoreObjects;
import one.util.streamex.EntryStream;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Simple envelope follower filter.
 */
public class EnvelopeFollowerFilter implements UnaryOperator<EntryStream<Integer, Double>>, Cloneable {

    /**
     * Coefficient moderating the adoption of higher signals.
     * A high value means slow adoption.
     */
    private final double attack;

    /**
     * Coefficient moderating the adoption of lower signals.
     * A high value means slow adoption.
     */
    private final double release;

    private double envelope;

    public EnvelopeFollowerFilter(double attack, double release) {
        this.attack = attack;
        this.release = release;
    }

    @Override
    public EntryStream<Integer, Double> apply(EntryStream<Integer, Double> values) {
        if (values.isParallel()) {
            throw new IllegalArgumentException("Unable to calculate envelope on parallel stream of values");
        }

        return values.mapValues(this::follow);
    }

    public Stream<Byte> apply(Stream<Byte> values) {
        if (values.isParallel()) {
            throw new IllegalArgumentException("Unable to calculate envelope on parallel stream of values");
        }

        return values.mapToDouble(Byte::doubleValue).map(this::follow).boxed().map(Double::byteValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("attack", attack).add("release", release).toString();
    }

    @Override
    public EnvelopeFollowerFilter clone() throws CloneNotSupportedException {
        return new EnvelopeFollowerFilter(attack, release);
    }

    private Double follow(Double value) {
        value = Math.abs(value);

        if (value > envelope) {
            envelope = value + (envelope - value) * attack;
        } else {
            envelope = value + (envelope - value) * release;
        }
        return envelope;
    }

}
