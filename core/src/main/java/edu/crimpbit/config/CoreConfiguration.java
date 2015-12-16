package edu.crimpbit.config;

import com.thalmic.myo.Hub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({ MongoConfiguration.class, ValidationConfiguration.class })
@ComponentScan({ "edu.crimpbit.service" })
public class CoreConfiguration {

    private static final String HUB_ID = "edu.crimpbit.analysis";

    @Bean
    public Hub hub() {
        return new Hub(HUB_ID);
    }

}
