package edu.crimpbit;

import com.thalmic.myo.Myo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * Analysis application entry point.
 */
@SpringBootApplication
public class Analysis {

    private static final Logger LOGGER = LoggerFactory.getLogger(Analysis.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalysisConfiguration.class, args);

        ConnectorService connectorService = context.getBean(ConnectorService.class);
        RecordingService recordingService = context.getBean(RecordingService.class);

        Collection<Myo> myos = connectorService.getMyos();
        myos.forEach(recordingService::startRecording);
        int ticks = 1000;
        while (0 < ticks--) {
            connectorService.getHub().run(1000/100);
        }
        myos.forEach(recordingService::stopRecording);
    }

    @Configuration
    public static class AnalysisConfiguration {

        @Bean
        public ConnectorService connectorService() {
            return new ConnectorService();
        }

        @Bean
        public RecordingService recordingService(ConnectorService connectorService) {
            return new RecordingService(connectorService);
        }

    }

}