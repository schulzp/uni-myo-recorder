package edu.crimpbit.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import edu.crimpbit.converter.StringToSubject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
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
    public CustomConversions customConversions() {
        return new CustomConversions(Arrays.asList(StringToSubject.INSTANCE));
    }

}
