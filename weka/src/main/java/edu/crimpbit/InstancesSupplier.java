package edu.crimpbit;

import com.google.common.util.concurrent.AtomicDouble;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by peter on 04/02/16.
 */
public class InstancesSupplier implements Supplier<Instances> {

    private final Instances instances;

    private Consumer<Instances> normalizer;

    public InstancesSupplier(Instances instances) {
        this.instances = instances;
    }

    @Override
    public Instances get() {
        return instances;
    }

    public Instances getNormalized() {
        Instances instances = get();
        getNormalizer().accept(instances);
        return instances;
    }

    public Consumer<Instances> getNormalizer() {
        if (normalizer == null) {
            normalizer = createNormalizer(instances);
        }
        return normalizer;
    }


    /**
     * Creates a normalizer function based on the specified instances.
     * @param trainingInstances the instances to analyze
     * @return a normalizer function
     */
    private Consumer<Instances> createNormalizer(Instances trainingInstances) {
        double numberOfValues = trainingInstances.numInstances() * 8;
        AtomicDouble valueSum = new AtomicDouble(0);

        forEach(trainingInstances.enumerateInstances(), (Instance instance) -> {
            for (int i = 0; i < 8; ++i) {
                valueSum.addAndGet(instance.value(i));
            }
        });

        double average = valueSum.get() / numberOfValues;

        AtomicDouble squaredErrorSum = new AtomicDouble(0);
        forEach(trainingInstances.enumerateInstances(), (Instance instance) -> {
            for (int i = 0; i < 8; ++i) {
                double error = instance.value(i) - average;
                squaredErrorSum.addAndGet(error * error);
            }
        });

        double variance = squaredErrorSum.get() / numberOfValues;
        double stddev = Math.sqrt(variance);

        return (normalizeInstances) -> {
            forEach(normalizeInstances.enumerateInstances(), (Instance instance) ->{
                for (int i = 0; i < 8; ++i) {
                    instance.setValue(i, (instance.value(i) - average) / stddev);
                }
            });
        };
    }

    private <T> void forEach(Enumeration<T> enumeration, Consumer<T> iterator) {
        while (enumeration.hasMoreElements()) {
            iterator.accept(enumeration.nextElement());
        }
    }

}
