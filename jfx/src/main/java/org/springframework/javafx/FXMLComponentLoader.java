package org.springframework.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import org.springframework.beans.BeanUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ResourceLoader;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * A wrapper for {@link FXMLLoader} utilizing spring.
 */
public class FXMLComponentLoader {

    public final ResourceLoader resourceLoader;

    private BuilderFactory builderFactory;
    private ResourceBundle resourceBundle;
    private Callback<Class<?>, Object> controllerFactory;

    public FXMLComponentLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setBuilderFactory(BuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    public void setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void load(Object bean, String beanName, String location) throws IOException, ReflectiveOperationException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(resourceLoader.getResource(location).getURL());
        loader.setControllerFactory(controllerFactory);
        loader.setBuilderFactory(builderFactory);
        loader.setResources(resourceBundle);
        loader.setController(bean);

        Object result = loader.load();

        if (result instanceof  Node) {
            ((Node) result).setId(beanName);
        }

        if (bean instanceof FXMLComponent.ParentAware) {
            PropertyDescriptor parentPropertyDescriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), "parent");
            MethodParameter parentMethodParameter = BeanUtils.getWriteMethodParameter(parentPropertyDescriptor);
            if (parentMethodParameter.getParameterType().isAssignableFrom(result.getClass())) {
                parentPropertyDescriptor.getWriteMethod().invoke(bean, result);
            } else {
                throw new NoSuchMessageException("Unexpected type of parent. Loaded parent is of type "
                        + result.getClass() + " which cannot be passed to " + parentMethodParameter.getMethod());
            }
        }
    }

}