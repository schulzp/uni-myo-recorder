package edu.crimpbit.anaylsis.converter;

import javafx.util.StringConverter;
import org.springframework.core.convert.ConversionService;

/**
 * StringConverter based on ConversionService.
 */
public class ConversionServiceStringConverter<T> extends StringConverter<T> {

    protected final ConversionService conversionService;

    protected final Class<T> type;

    public ConversionServiceStringConverter(ConversionService conversionService, Class<T> type) {
        this.type = type;
        this.conversionService = conversionService;
    }

    @Override
    public String toString(T subject) {
        return conversionService.convert(subject, String.class);
    }

    @Override
    public T fromString(String string) {
        return conversionService.convert(string, type);
    }

}
