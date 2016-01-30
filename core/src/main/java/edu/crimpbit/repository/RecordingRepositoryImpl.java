package edu.crimpbit.repository;

import edu.crimpbit.Recording;
import edu.crimpbit.Subject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Query query = getQuery(id, gesture, tag);
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
        return findBySubjectIdAndGestureAndTag(subjectRepository.findByName(name).get(0).getId(), gesture, tag);
    }

//    @Override
//    public List<Recording> findBySubjectNamesAndTagsAndGestures(List<String> names, List<String> tags, List<String> gestures) {
//        List<String> ids = names.stream().map(name -> subjectRepository.findByName(name).get(0).getId()).collect(Collectors.toList());
//        Query query = getQuery(ids, gestures, tags);
//        System.out.println(query);
//        return mongoTemplate.find(query, Recording.class);
//    }

    /**
     * builds query
     *
     * @param id      subject id
     * @param gesture gesture name
     * @param tag     tag name
     * @return query that contains criteria for all params that are valid
     */
    private Query getQuery(String id, String gesture, String tag) {
        Query query = new Query();
        if (id != null && !id.isEmpty()) {
            query = query.addCriteria(Criteria.where("subject._id").is(new ObjectId(id)));
        }

        if (gesture != null && !gesture.isEmpty()) {
            query = query.addCriteria(Criteria.where("gesture.name").is(gesture));
        }

        if (tag != null && !tag.isEmpty()) {
            query = query.addCriteria(Criteria.where("tag.name").is(tag));
        }

        return query;
    }

//    private Query getQuery(List<String> ids, List<String> tags, List<String> gestures) {
//        Query query = new Query();
//        if (ids != null) {
//            Criteria idsCriteria = new Criteria();
//            for (String id : ids) {
//                if (id != null && !id.isEmpty()) {
//                    idsCriteria.
//                    query.addCriteria(Criteria.where("subject._id").is(new ObjectId(id)).);
//                }
//            }
//        }
//        if (gestures != null) {
//            // for (String gesture : gestures) {
//            //   if (gesture != null && !gesture.isEmpty()) {
//            query.addCriteria(Criteria.where("gesture.name").is(gestures));
//            // }
//            //}
//        }
//        if (tags != null) {
//            //for (String tag : tags) {
//            //  if (tag != null && !tag.isEmpty()) {
//            query.addCriteria(Criteria.where("tag.name").all(tags));
//            //}
//            //}
//        }
//        return query;
//    }
}
