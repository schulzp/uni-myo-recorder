package edu.crimpbit.converter;

import edu.crimpbit.Subject;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Converter from String to {@link Subject}.
 */
@ReadingConverter
public enum StringToSubject implements GenericConverter {

    INSTANCE;

    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair stringToSubject = new ConvertiblePair(String.class, Subject.class);
        return new HashSet<>(Arrays.asList(stringToSubject));
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Subject subject = new Subject();
        subject.setName(source.toString());
        return subject;
    }

}
