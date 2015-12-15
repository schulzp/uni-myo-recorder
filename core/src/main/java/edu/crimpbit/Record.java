package edu.crimpbit;

/**
 * Base class for a record of data at a certain point of time.
 * @param <T> data type
 */
public abstract class Record<T> {

    protected final long timestamp;

    public Record(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract T getData();

}
