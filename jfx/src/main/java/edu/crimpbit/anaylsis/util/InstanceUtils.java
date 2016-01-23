package edu.crimpbit.anaylsis.util;

import java.util.function.Consumer;

/**
 * Instance utility functions.
 */
public abstract class InstanceUtils {

    public static <T> T with(T instance, Consumer<T> consumer) {
        consumer.accept(instance);
        return instance;
    }

}
