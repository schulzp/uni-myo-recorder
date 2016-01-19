package edu.crimpbit;

import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Data with meta information.
 */
@Document(collection = "recording")
public class Recording {

    @Id
    private String id;

    @NotNull
    private final Arm arm;

    @NotNull
    private final XDirection xDirection;

    @NotNull
    private final float rotation;

    @NotNull
    private final LocalTime createdAt = LocalTime.now();

    @NotNull
    private String exercise;

    @Reference
    private Subject subject;

    @DBRef
    private final EMGData emgData = new EMGData();

    public Recording(Arm arm, XDirection xDirection, float rotation) {
        this.arm = arm;
        this.xDirection = xDirection;
        this.rotation = rotation;
    }

    public Arm getArm() {
        return arm;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getExercise() {
        return exercise;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public EMGData getEmgData() {
        return emgData;
    }

    public String getId() {
        return id;
    }

    public static class EmgRecord extends Record<byte[]> {

        private final byte[] data;

        public EmgRecord(long timestamp, byte[] data) {
            super(timestamp);
            this.data = data;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "EMG at " + timestamp + ": " + Arrays.toString(data);
        }

    }

}
