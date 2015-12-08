package org.springframework.javafx;

import javafx.scene.Parent;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Extends {@link Component} to be FXML resource aware.
 */
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface FXMLComponent {

    String DEFAULT_LOCATION = "";

    /**
     * FXML resource location.
     * @return location of the FXML resource
     */
    String location() default DEFAULT_LOCATION;

    /**
     * Marks a {@link FXMLComponent} annotated as parent aware.
     * @param <T> parent type
     */
    interface ParentAware<T extends Parent> {

        void setParent(T parent);

        T getParent();

    }

}
