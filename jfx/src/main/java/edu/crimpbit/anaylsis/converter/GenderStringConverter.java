package edu.crimpbit.anaylsis.converter;

import edu.crimpbit.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * {@link EnumStringConverter} for {@link Subject.Gender}.
 */
@Component
public class GenderStringConverter extends EnumStringConverter<Subject.Gender> {

    @Autowired
    public GenderStringConverter(ResourceBundle resourceBundle) {
        super(Subject.Gender.class, "subject.gender.", resourceBundle);
    }

}
