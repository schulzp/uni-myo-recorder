package edu.crimpbit.filter;

import com.google.common.base.MoreObjects;

import java.util.function.Function;

/**
 * Simple envelope follower filter.
 */
public class EnvelopeFollowerFilter implements Function<Double, Double>, Cloneable {

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

    private double envelope = 0;

    public EnvelopeFollowerFilter(double attack, double release) {
        this.attack = attack;
        this.release = release;
    }

    @Override
    public Double apply(Double value) {
        value = Math.abs(value);
        if (value > envelope) {
            envelope = value + (envelope - value) * attack;
        } else {
            envelope = value + (envelope - value) * release;
        }
        return envelope;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("attack", attack).add("release", release).toString();
    }

    @Override
    public EnvelopeFollowerFilter clone() throws CloneNotSupportedException {
        return new EnvelopeFollowerFilter(attack, release);
    }

}
