package edu.crimpbit;

import java.util.LinkedList;
import java.util.List;

/**
 * Gesture representation.
 */
public class Gesture {

    private final String name;

    private List<String> tags = new LinkedList<>();

    public Gesture(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
