package edu.crimpbit.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = { "edu.crimpbit.repository" })
@PropertySource("classpath:database.properties")
public class MongoConfiguration {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.database}")
    private String mongoDatabase;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoClientOptions mongoOptions = new MongoClientOptions.Builder().maxWaitTime(1000 * 60 * 5).build();
        MongoClient client = new MongoClient(mongoHost, mongoOptions);
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(client, mongoDatabase);
        return new MongoTemplate(mongoDbFactory);
    }

}
