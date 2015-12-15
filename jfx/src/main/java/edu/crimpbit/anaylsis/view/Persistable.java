package edu.crimpbit.anaylsis.view;

import javafx.beans.property.ReadOnlyStringProperty;

public interface Persistable<T> {

    ReadOnlyStringProperty textProperty();

    void save();

}
