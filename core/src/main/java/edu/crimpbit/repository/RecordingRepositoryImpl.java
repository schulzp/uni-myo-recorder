package edu.crimpbit.repository;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Recording;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by joerg on 25.01.16.
 */
public class RecordingRepositoryImpl implements RecordingRepositoryExtension {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<Recording> findBySubjectIdAndGestureAndTag(String id, String gesture, String tag) {
        Query query = getQuery(id, null, gesture, tag);
        return find(query);
    }

    private List<Recording> find(Query query) {
        return mongoTemplate.find(query, Recording.class);
    }

    @Override
    public List<Recording> findBySubjectIdAndGesture(String id, String gesture) {
        return findBySubjectIdAndGestureAndTag(id, gesture, null);
    }

    @Override
    public List<Recording> findBySubjectIdAndTag(String id, String tag) {
        return findBySubjectIdAndGestureAndTag(id, null, tag);
    }

    @Override
    public List<Recording> findBySubjectNameAndTagAndGesture(String name, String tag, String gesture) {
        if (name != null) {
            return findBySubjectIdAndGestureAndTag(subjectRepository.findByName(name).get(0).getId(), gesture, tag);
        } else {
            return findBySubjectIdAndGestureAndTag(null, gesture, tag);
        }
    }

    @Override
    public List<Recording> findBySubjectIdAndArmAndTagAndGesture(String id, Arm arm, String tag, String gesture) {
        return find(getQuery(id, arm, tag, gesture));
    }

    /**
     * builds query
     *
     * @param id      subject id
     * @param arm     subject arm
     * @param gesture gesture name
     * @param tag     tag name
     * @return query that contains criteria for all params that are valid
     */
    private Query getQuery(String id, Arm arm, String gesture, String tag) {
        Query query = new Query();
        if (id != null && !id.isEmpty()) {
            query.addCriteria(Criteria.where("subject.$id").is(new ObjectId(id)));
        }

        if (arm != null) {
            query.addCriteria(Criteria.where("arm").is(arm.name()));
        }

        if (gesture != null && !gesture.isEmpty()) {
            query.addCriteria(Criteria.where("gesture.name").is(gesture));
        }

        if (tag != null && !tag.isEmpty()) {
            query.addCriteria(Criteria.where("gesture.tags").in(tag));
        }

        return query;
    }

}
