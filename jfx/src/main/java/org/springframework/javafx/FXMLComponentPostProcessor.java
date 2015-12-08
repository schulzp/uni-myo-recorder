package org.springframework.javafx;

import javafx.util.BuilderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * BeanPostProcessor for {@link FXMLComponent} annotation.
 */
@Component
public class FXMLComponentPostProcessor implements BeanPostProcessor {

    private final FXMLComponentLoader loader;

    private String defaultLocationPrefix = "/fxml/";
    private String defaultLocationSuffix = ".fxml";

    public FXMLComponentPostProcessor(FXMLComponentLoader loader) {
        this.loader = loader;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        loader.setResourceBundle(resourceBundle);
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        loader.setBuilderFactory(builderFactory);
    }

    public void setDefaultLocationPrefix(String defaultLocationPrefix) {
        this.defaultLocationPrefix = defaultLocationPrefix;
    }

    public void setDefaultLocationSuffix(String defaultLocationSuffix) {
        this.defaultLocationSuffix = defaultLocationSuffix;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        FXMLComponent annotation = AnnotationUtils.findAnnotation(beanClass, FXMLComponent.class);
        if (annotation != null) {
            try {
                String location = getLocation(annotation).orElseGet(() -> getDefaultLocation(beanClass));
                load(bean, beanName, location);
            } catch (IOException|ReflectiveOperationException e) {
                throw new BeanInitializationException("Failed to load FXML component " + bean, e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    protected String getDefaultLocation(Class<?> beanClass) {
        return defaultLocationPrefix + beanClass.getSimpleName() + defaultLocationSuffix;
    }

    protected void load(Object bean, String beanName, String location) throws IOException, ReflectiveOperationException {
        loader.load(bean, beanName, location);
    }

    private Optional<String> getLocation(FXMLComponent annotation) {
        String location = annotation.location();
        if (location.equals(FXMLComponent.DEFAULT_LOCATION)) {
            return Optional.empty();
        }
        return Optional.of(location);
    }

}
