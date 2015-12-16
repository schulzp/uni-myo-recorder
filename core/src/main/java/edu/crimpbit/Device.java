package edu.crimpbit;

import com.google.common.base.MoreObjects;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import javafx.beans.property.*;

import java.util.stream.IntStream;

/**
 * Device representation.
 */
public class Device {

    public static final int NUM_EMG_PADS = 8;

    public static IntStream createEmgPadIndexStream() {
        return IntStream.range(0, NUM_EMG_PADS);
    }

    public enum Status {

        UNPAIRED, PAIRED, DISCONNECTED, CONNECTED, IN_SYNC, OUT_OF_SYNC

    }

    private final Myo myo;

    private final Hub hub;

    private final StringProperty name = new SimpleStringProperty();

    private final ReadOnlyObjectWrapper<Status> status = new ReadOnlyObjectWrapper<>(Status.UNPAIRED);

    private final ReadOnlyBooleanWrapper locked = new ReadOnlyBooleanWrapper();

    private final ReadOnlyObjectWrapper<Arm> arm = new ReadOnlyObjectWrapper<>(Arm.ARM_UNKNOWN);

    private final ReadOnlyObjectWrapper<XDirection> xDirection = new ReadOnlyObjectWrapper<>(XDirection.X_DIRECTION_UNKNOWN);

    private final ReadOnlyFloatWrapper rotation = new ReadOnlyFloatWrapper();

    public Device(Myo myo, Hub hub) {
        this.myo = myo;
        this.hub = hub;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public boolean isLocked() {
        return locked.get();
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
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

    public float getRotation() {
        return rotation.get();
    }

    public void setRotation(float rotation) {
        this.rotation.set(rotation);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ReadOnlyObjectWrapper<Status> statusProperty() {
        return status;
    }

    public ReadOnlyBooleanWrapper lockedProperty() {
        return locked;
    }

    public ReadOnlyObjectProperty<Arm> armProperty() {
        return arm;
    }

    public ReadOnlyObjectProperty<XDirection> xDirectionProperty() {
        return xDirection;
    }

    public ReadOnlyFloatProperty rotationProperty() {
        return rotation;
    }

    public Myo getMyo() {
        return myo;
    }

    public Hub getHub() {
        return hub;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(name.getValue())
                .add("arm", arm.getValue())
                .add("x-direction", xDirection)
                .add("rotation", rotation)
                .add("status", status)
                .add("locked", locked.getValue())
                .toString();
    }
}
