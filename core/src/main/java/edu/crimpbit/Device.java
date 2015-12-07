package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import javafx.beans.property.*;

/**
 * Device representation.
 */
public class Device {

    private final Myo myo;

    private final StringProperty name = new SimpleStringProperty();

    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    private final ReadOnlyBooleanWrapper locked = new ReadOnlyBooleanWrapper();

    private final BooleanProperty selected = new SimpleBooleanProperty();

    private final ObjectProperty<Arm> arm = new SimpleObjectProperty<>(Arm.ARM_UNKNOWN);

    private final ObjectProperty<XDirection> xDirection = new SimpleObjectProperty<>(XDirection.X_DIRECTION_UNKNOWN);

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

    void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public boolean isLocked() {
        return locked.get();
    }

    void setLocked(boolean locked) {
        this.locked.set(locked);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public Arm getArm() {
        return arm.get();
    }

    public void setArm(Arm arm) {
        this.arm.set(arm);
    }

    public XDirection getXDirection() {
        return xDirection.get();
    }

    public void setXDirection(XDirection xDirection) {
        this.xDirection.set(xDirection);
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

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ObjectProperty<Arm> armProperty() {
        return arm;
    }

    public ObjectProperty<XDirection> xDirectionProperty() {
        return xDirection;
    }

    Myo getMyo() {
        return myo;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(name.getValue())
                .add("arm", arm.getValue())
                .add("connected", connected.getValue())
                .add("locked", locked.getValue())
                .add("selected", selected.getValue())
                .toString();
    }
}
