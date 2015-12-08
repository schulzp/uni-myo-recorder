package edu.crimpbit.anaylsis.main;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface FXMLComponent {

    /**
     * FXML resource location.
     * @return location of the FXML resource
     */
    String location();

}
