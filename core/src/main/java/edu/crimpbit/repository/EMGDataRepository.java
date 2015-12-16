package edu.crimpbit.repository;

import edu.crimpbit.EMGData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EMGDataRepository extends MongoRepository<EMGData, String> {

}
