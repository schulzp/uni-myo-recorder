package edu.crimpbit.repository;

import edu.crimpbit.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {

    List<Subject> findByName(String name);

}
