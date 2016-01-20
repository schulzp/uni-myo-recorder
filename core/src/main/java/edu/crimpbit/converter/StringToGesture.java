package edu.crimpbit.converter;

import edu.crimpbit.Gesture;
import edu.crimpbit.Subject;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Converter from String to {@link Subject}.
 */
@ReadingConverter
@WritingConverter
public enum StringToGesture implements GenericConverter {

    INSTANCE;

    private static final HashSet<ConvertiblePair> CONVERTIBLE_PAIRS = new HashSet<>(Arrays.asList(
            new ConvertiblePair(String.class, Gesture.class),
            new ConvertiblePair(Gesture.class, String.class)));

    public Set<ConvertiblePair> getConvertibleTypes() {
        return CONVERTIBLE_PAIRS;
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source instanceof String) {
            new Gesture((String) source);
        }
        return ((Gesture) source).getName();
    }

}
