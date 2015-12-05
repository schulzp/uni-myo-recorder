package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.thalmic.myo.Myo;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Device representation.
 */
public class Device {

    private final Myo myo;

    private final StringProperty name = new SimpleStringProperty();

    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    private final ReadOnlyBooleanWrapper locked = new ReadOnlyBooleanWrapper();

    public Device(Myo myo) {
        this.myo = myo;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public boolean isLocked() {
        return locked.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected;
    }

    public ReadOnlyBooleanWrapper lockedProperty() {
        return locked;
    }

    Myo getMyo() {
        return myo;
    }

    void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name.getValue())
                .add("connected", connected.getValue())
                .add("locked", locked.getValue()).toString();
    }
}
