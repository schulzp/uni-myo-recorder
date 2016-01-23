package edu.crimpbit.anaylsis.converter;

import com.thalmic.myo.enums.Arm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * {@link EnumStringConverter} for {@link Arm}.
 */
@Component
public class ArmStringConverter extends EnumStringConverter<Arm> {

    @Autowired
    public ArmStringConverter(ResourceBundle resourceBundle) {
        super(Arm.class, "device.arm.", resourceBundle);
    }

}