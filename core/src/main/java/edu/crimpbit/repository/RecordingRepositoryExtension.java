package edu.crimpbit.repository;

import edu.crimpbit.Recording;

import java.util.List;

/**
 * Created by joerg on 25.01.16.
 * extension for RecordingRepository interface. Provides filtered Recordings.
 */
public interface RecordingRepositoryExtension {
    /**
     * finds recording with given id (@param id), gesture name (@param gesture) and tag name (@param tag)
     * @param id subject id
     * @param gesture gesture name
     * @param tag tag name
     * @return list of recordings filtered by given criteria
     */
    public List<Recording> findBySubjectIdAndGestureAndTag(long id, String gesture, String tag);

    /**
     * finds recording with given id (@param id) and gesture name (@param gesture)
     * @param id subject id
     * @param gesture gesture name
     * @return list of recordings filtered by given criteria
     */
    public List<Recording> findBySubjectIdAndGesture(long id, String gesture);

    /**
     * finds recording with given id (@param id) and tag name (@param tag)
     * @param id subject id
     * @param tag tag name
     * @return list of recordings filtered by given criteria
     */
    public List<Recording> findBySubjectIdAndTag(long id, String tag);
}
