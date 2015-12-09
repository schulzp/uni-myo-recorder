package org.springframework.javafx;

import javafx.util.Callback;
import org.springframework.beans.factory.BeanFactory;

/**
 * A spring based controller factory.
 */
public class FXMLComponentControllerFactory implements Callback<Class<?>, Object> {

    private final BeanFactory beanFactory;

    public FXMLComponentControllerFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object call(Class<?> type) {
        return beanFactory.getBean(type);
    }

}
