package edu.crimpbit.anaylsis.converter;

import edu.crimpbit.Gesture;
import edu.crimpbit.Subject;
import org.springframework.core.convert.ConversionService;

/**
 * ConversionServiceStringConverter for Gesture.
 */
public class GestureStringConverter extends ConversionServiceStringConverter<Gesture> {

    public GestureStringConverter(ConversionService conversionService) {
        super(conversionService, Gesture.class);
    }

}