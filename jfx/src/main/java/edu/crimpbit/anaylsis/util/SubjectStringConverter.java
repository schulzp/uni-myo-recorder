package edu.crimpbit.anaylsis.util;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Subject;
import edu.crimpbit.service.SubjectService;
import javafx.util.StringConverter;

/**
 * {@link StringConverter} implementation for {@link Arm} values.
 */
public class SubjectStringConverter extends StringConverter<Subject> {

    private final SubjectService subjectService;

    public SubjectStringConverter(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Override
    public String toString(Subject subject) {
        return subject.getName();
    }

    @Override
    public Subject fromString(String string) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}