package edu.crimpbit.anaylsis.converter;

import edu.crimpbit.Gesture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

/**
 * ConversionServiceStringConverter for Gesture.
 */
@Component
public class GestureStringConverter extends ConversionServiceStringConverter<Gesture> {

    @Autowired
    public GestureStringConverter(ConversionService conversionService) {
        super(conversionService, Gesture.class);
    }

}