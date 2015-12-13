package edu.crimpbit.anaylsis.command;

import com.google.common.base.CaseFormat;

/**
 * Command representation.
 */
public interface Command<T> extends Runnable {

    default String getId() {
        return CaseFormat.UPPER_CAMEL.to(
                CaseFormat.LOWER_UNDERSCORE, getClass().getSimpleName()).replace('_', '.');
    }

}
