package edu.crimpbit.anaylsis.converter;

import edu.crimpbit.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

/**
 * ConversionServiceStringConverter for Subject.
 */
@Component
public class SubjectStringConverter extends ConversionServiceStringConverter<Subject> {

    @Autowired
    public SubjectStringConverter(ConversionService conversionService) {
        super(conversionService, Subject.class);
    }

}