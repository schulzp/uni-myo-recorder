package edu.crimpbit.anaylsis.util;

import com.thalmic.myo.enums.Arm;
import javafx.util.StringConverter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link StringConverter} implementation for {@link Arm} values.
 */
public class ArmStringConverter extends StringConverter<Arm> {

    static final String RESOURCE_BUNDLE_PREFIX = "device.arm.";

    final ResourceBundle resourceBundle;

    final Map<Arm, String> translations;

    public ArmStringConverter(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        translations = new HashMap<>();
    }

    @Override
    public String toString(Arm arm) {
        loadTranslations();

        return translations.get(arm);
    }

    @Override
    public Arm fromString(String string) {
        loadTranslations();

        return translations.entrySet().stream()
                .filter(translation -> translation.getValue().equals(string)).findFirst()
                .map(translation -> translation.getKey())
                .orElseThrow(() -> new NoSuchElementException("Failed to map '" + string + "' to one of " + Arrays.toString(Arm.values())));
    }

    private void loadTranslations() {
        if (translations.size() == 0) {
            Arrays.stream(Arm.values()).forEach(arm -> translations.put(arm, resourceBundle.getString(RESOURCE_BUNDLE_PREFIX + arm.name())));
        }
    }

}