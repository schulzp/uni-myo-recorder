package edu.crimpbit.repository;

import edu.crimpbit.Recording;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordingRepository extends MongoRepository<Recording, String> {

}
