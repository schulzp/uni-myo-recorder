package edu.crimpbit.config;

import edu.crimpbit.repository.RecordingRepository;
import edu.crimpbit.service.ConnectorService;
import edu.crimpbit.service.RecordingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({ MongoConfiguration.class })
public class CoreConfiguration {

    @Bean
    public ConnectorService connectorService() {
        return new ConnectorService();
    }

    @Bean
    public RecordingService recordingService(ConnectorService connectorService, RecordingRepository recordingRepository) {
        return new RecordingService(connectorService, recordingRepository);
    }

}
