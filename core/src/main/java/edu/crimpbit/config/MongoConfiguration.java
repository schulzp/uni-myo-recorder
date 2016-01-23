package edu.crimpbit.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import edu.crimpbit.Recording;
import edu.crimpbit.Subject;
import edu.crimpbit.converter.StringToGesture;
import edu.crimpbit.converter.StringToSubject;
import edu.crimpbit.repository.RepositoryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@Configuration
@EnableMongoRepositories(basePackages = { "edu.crimpbit.repository" })
@PropertySource("classpath:database.properties")
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.database}")
    private String mongoDatabase;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    public Mongo mongo() throws Exception {
        MongoClientOptions mongoOptions = new MongoClientOptions.Builder().maxWaitTime(1000 * 60 * 5).build();
        return new MongoClient(mongoHost, mongoOptions);
    }

    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = super.mongoTemplate();
        createIndices(mongoTemplate);
        return mongoTemplate;
    }

    private void createIndices(MongoTemplate mongoTemplate) {
        mongoTemplate.indexOps(Subject.class).ensureIndex(new Index().on("name", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(Recording.class).ensureIndex(new Index().on("gesture", Sort.Direction.ASC));
        mongoTemplate.indexOps(Recording.class).ensureIndex(new Index().on("gesture.tags", Sort.Direction.ASC));
    }

    @Bean
    public ApplicationListener<MongoMappingEvent<?>> mongoEventListener(ApplicationEventPublisher applicationEventPublisher) {

        return new AbstractMongoEventListener<Object>() {

            @Override
            public void onAfterSave(AfterSaveEvent<Object> event) {
                applicationEventPublisher.publishEvent("update." + event.getCollectionName());
            }

            @Override
            public void onAfterDelete(AfterDeleteEvent<Object> event) {
                applicationEventPublisher.publishEvent("update." + event.getCollectionName());
            }

        };

    }

    @Bean
    public RepositoryProvider repositoryProvider() {
        return new RepositoryProvider();
    }

}
