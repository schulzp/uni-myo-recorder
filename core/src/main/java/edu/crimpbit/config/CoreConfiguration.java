package edu.crimpbit.config;

import com.thalmic.myo.Hub;
import edu.crimpbit.converter.StringToGesture;
import edu.crimpbit.converter.StringToSubject;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;


@Configuration
@Import({ MongoConfiguration.class, ValidationConfiguration.class })
@ComponentScan({ "edu.crimpbit.service" })
@PropertySource({ "core.properties" })
public class CoreConfiguration {

    private static final String HUB_ID = "edu.crimpbit.analysis";

    @Bean
    public Hub hub() {
        return new Hub(HUB_ID);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public GenericConversionService genericConversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public StringToGesture stringToGesture(GenericConversionService conversionService) {
        conversionService.addConverter(StringToGesture.INSTANCE);
        return StringToGesture.INSTANCE;
    }

    @Bean
    public StringToSubject stringToSubject(GenericConversionService conversionService) {
        conversionService.addConverter(StringToSubject.INSTANCE);
        return StringToSubject.INSTANCE;
    }

}
