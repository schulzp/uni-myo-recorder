package edu.crimpbit;

import com.thalmic.myo.enums.Arm;
import com.thalmic.myo.enums.XDirection;
import org.springframework.data.annotation.Id;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Data with meta information.
 */
public class Recording {

    @Id
    private String id;

    private final Arm arm;

    private final XDirection xDirection;

    private final float rotation;

    private final LocalTime createdAt = LocalTime.now();

    private String title;

    private String person;

    private List<EmgRecord> emgRecords = new LinkedList<>();

    public Recording(Arm arm, XDirection xDirection, float rotation) {
        this.arm = arm;
        this.xDirection = xDirection;
        this.rotation = rotation;
    }

    public List<EmgRecord> getEmgRecords() {
        return emgRecords;
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
