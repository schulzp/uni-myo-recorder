package org.springframework.javafx;

import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A spring backed {@link BuilderFactory}.
 */
public class FXMLComponentBuilderFactory implements BuilderFactory, BeanFactoryAware {

    private static final Builder<Void> NULL_BUILDER = new Builder<Void>() {

        @Override
        public Void build() {
            throw new UnsupportedOperationException("This is a null not intended to be used.");
        }

        @Override
        public String toString() {
            return "NULL_BUILDER";
        }

    };

    private BeanFactory beanFactory;

    private final Map<Class<?>, Builder<?>> builders = new HashMap<>();

    @Override
    public Builder<?> getBuilder(Class<?> type) {
        Builder<?> builder = builders.computeIfAbsent(type, t -> {
            Map<String, ?> beansOfType = ((ListableBeanFactory) beanFactory).getBeansOfType(type, true, false);
            if (beansOfType.isEmpty()) {
                return NULL_BUILDER;
            }
            return () -> beanFactory.getBean(type);
        });

        return builder == NULL_BUILDER ? null : builder;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
