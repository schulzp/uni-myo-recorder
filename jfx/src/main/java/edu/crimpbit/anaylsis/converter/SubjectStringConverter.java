package edu.crimpbit.anaylsis.converter;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Subject;
import javafx.util.StringConverter;
import org.springframework.core.convert.ConversionService;

/**
 * ConversionServiceStringConverter for Subject.
 */
public class SubjectStringConverter extends ConversionServiceStringConverter<Subject> {

    public SubjectStringConverter(ConversionService conversionService) {
        super(conversionService, Subject.class);
    }

}