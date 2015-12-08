package org.springframework.javafx;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.core.io.ResourceLoader;

import java.util.ResourceBundle;

/**
 * Default configuration
 */
@Configuration
public class FXMLComponentConfiguration {

    @Bean
    public ResourceBundle fxmlComponentResourceBundle(MessageSource messageSource) {
        return new MessageSourceResourceBundle(messageSource, LocaleContextHolder.getLocale());
    }

    @Bean
    public FXMLComponentBuilderFactory fxmlComponentBuilderFactory() {
        return new FXMLComponentBuilderFactory();
    }

    @Bean
    public FXMLComponentLoader fxmlComponentLoader(ResourceLoader resourceLoader,
                                                   FXMLComponentBuilderFactory fxmlComponentBuilderFactory,
                                                   @Qualifier("fxmlComponentResourceBundle") ResourceBundle resourceBundle) {
        FXMLComponentLoader fxmlComponentLoader = new FXMLComponentLoader(resourceLoader);
        fxmlComponentLoader.setBuilderFactory(fxmlComponentBuilderFactory);
        fxmlComponentLoader.setResourceBundle(resourceBundle);
        return fxmlComponentLoader;
    }

    @Bean
    public FXMLComponentPostProcessor fxmlComponentPostProcessor(FXMLComponentLoader fxmlComponentLoader) {
        return new FXMLComponentPostProcessor(fxmlComponentLoader);
    }

}
