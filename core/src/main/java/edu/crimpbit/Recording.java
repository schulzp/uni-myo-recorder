package edu.crimpbit;

import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private final LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private Gesture gesture;

    @DBRef
    @NotNull
    private Subject subject;

    @DBRef
    @NotNull
    private final EMGData emgData = new EMGData();

    public Recording(Arm arm, XDirection xDirection, float rotation) {
        this.arm = arm;
        this.xDirection = xDirection;
        this.rotation = rotation;
    }

    public Arm getArm() {
        return arm;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

    public Gesture getGesture() {
        return gesture;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
