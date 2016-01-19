package edu.crimpbit.service;

import edu.crimpbit.Subject;
import edu.crimpbit.repository.SubjectRepository;
import javafx.collections.FXCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Subject} service.
 */
@Service
public class SubjectService {

    private final SubjectRepository repository;

    @Autowired
    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    public List<Subject> findAll() {
        return FXCollections.observableList(repository.findAll());
    }

    public void save(Subject subject) {
        repository.save(subject);
    }
}
