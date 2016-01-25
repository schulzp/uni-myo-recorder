package edu.crimpbit.repository;

import edu.crimpbit.Recording;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by jorg on 25.01.16.
 */
public class RecordingRepositoryImpl implements RecordingRepositoryExtension {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Recording> findBySubjectIdAndGestureAndTag(long id, String gesture, String tag) {
        Query query = getQuery(id, gesture, tag);
        return mongoTemplate.find(query, Recording.class);
    }

    @Override
    public List<Recording> findBySubjectIdAndGesture(long id, String gesture) {
        return findBySubjectIdAndGestureAndTag(id, gesture, null);
    }

    @Override
    public List<Recording> findBySubjectIdAndTag(long id, String tag) {
        return findBySubjectIdAndGestureAndTag(id, null, tag);
    }

    /**
     * builds query
     * @param id subject id
     * @param gesture gesture name
     * @param tag tag name
     * @return query that contains criteria for all params that are valid
     */
    private Query getQuery(long id, String gesture, String tag) {
        Query query = new Query();
        if(id >= 0) {
            query = query.addCriteria(Criteria.where("subject.§id").is(id));
        }

        if(gesture != null && !gesture.isEmpty()){
            query = query.addCriteria(Criteria.where("gesture.name").is(gesture));
        }

        if(tag != null && !tag.isEmpty()){
            query = query.addCriteria(Criteria.where("tag.name").is(tag));
        }

        return query;
    }
}
