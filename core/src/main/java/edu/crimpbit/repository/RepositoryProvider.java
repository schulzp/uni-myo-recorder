package edu.crimpbit.repository;

import edu.crimpbit.Recording;
import edu.crimpbit.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.NoSuchElementException;

/**
 * Generic entity to repository mapper.
 */
public class RepositoryProvider {

    @Autowired
    private RecordingRepository recordingRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public <T> CrudRepository<T, ?> get(T entity) {
        if (entity instanceof Recording) {
            return (CrudRepository<T, ?>) recordingRepository;
        }
        if (entity instanceof Subject) {
            return (CrudRepository<T, ?>) subjectRepository;
        }
        throw new NoSuchElementException("No repository found for " + entity);
    }

}
