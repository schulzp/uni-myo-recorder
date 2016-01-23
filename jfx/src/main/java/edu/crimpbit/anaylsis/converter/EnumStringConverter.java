package edu.crimpbit.anaylsis.converter;

import com.thalmic.myo.enums.Arm;
import javafx.util.StringConverter;

import java.util.*;

/**
 * {@link StringConverter} implementation for enum constants.
 */
public abstract class EnumStringConverter<T extends Enum<T>> extends StringConverter<T> {

    private final String resourcePrefix;

    private final ResourceBundle resourceBundle;

    private final Map<T, String> translations = new HashMap<>();

    public EnumStringConverter(Class<T> type, String resourcePrefix, ResourceBundle resourceBundle) {
        this.resourcePrefix = resourcePrefix;
        this.resourceBundle = resourceBundle;

        Arrays.stream(type.getEnumConstants())
                .forEach(constant -> translations.put(constant, resourceBundle.getString(this.resourcePrefix + constant.name())));
    }

    @Override
    public String toString(T value) {
        return translations.get(value);
    }

    @Override
    public T fromString(String string) {
        return translations.entrySet().stream()
                .filter(translation -> translation.getValue().equals(string)).findFirst()
                .map(translation -> translation.getKey())
                .orElseThrow(() -> new NoSuchElementException("Failed to map '" + string + "' to one of " + Arrays.toString(Arm.values())));
    }

}