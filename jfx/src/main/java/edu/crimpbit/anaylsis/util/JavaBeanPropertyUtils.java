package edu.crimpbit.anaylsis.util;

import javafx.beans.property.adapter.*;

/**
 * JavaFX bean property utility functions.
 */
public abstract class JavaBeanPropertyUtils {

    /**
     * Creates a bean property wrapper.
     * @param bean the bean
     * @param propertyName the property name
     * @param propertyType the property type
     * @param <T> type of the property
     * @return
     */
    public static <T> JavaBeanProperty<T> getProperty(Object bean, String propertyName, Class<? extends T> propertyType) {
        try {
            if (Number.class.isAssignableFrom(propertyType)) {
                if (propertyType == Integer.class) {
                    return (JavaBeanProperty<T>) JavaBeanIntegerPropertyBuilder.create()
                            .bean(bean).beanClass(bean.getClass()).name(propertyName).build();
                }
                if (propertyType == Double.class) {
                    return (JavaBeanProperty<T>) JavaBeanDoublePropertyBuilder.create()
                            .bean(bean).beanClass(bean.getClass()).name(propertyName).build();
                }
            } else if (propertyType == String.class) {
                return (JavaBeanProperty<T>) JavaBeanStringPropertyBuilder.create()
                        .bean(bean).beanClass(bean.getClass()).name(propertyName).build();
            } else {
                return (JavaBeanProperty<T>) JavaBeanObjectPropertyBuilder.create()
                        .bean(bean).beanClass(bean.getClass()).name(propertyName).build();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to obtain name property.", e);
        }
        throw new IllegalArgumentException("Unable to create bean property for type " + propertyType);
    }

    private JavaBeanPropertyUtils() {

    }

}
