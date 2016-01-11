package edu.crimpbit;

import org.springframework.data.annotation.Id;

import java.util.LinkedList;
import java.util.List;

/**
 * Document base class for raw data recordings.
 */
public abstract class RawData<T> {

    @Id
    private String id;

    protected final List<Long> timestamps = new LinkedList<>();

    public synchronized int size() {
        return timestamps.size();
    }

    public String getId() {
        return id;
    }

    public abstract void add(long timestamp, T value);

}
