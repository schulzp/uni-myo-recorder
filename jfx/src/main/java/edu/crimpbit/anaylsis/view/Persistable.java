package edu.crimpbit.anaylsis.view;

import javafx.beans.value.ObservableBooleanValue;

public interface Persistable<T> {

    void setPersistable(T persitable);

    ObservableBooleanValue dirtyValue();

    void save();

}
