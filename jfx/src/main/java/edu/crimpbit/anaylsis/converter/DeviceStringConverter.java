package edu.crimpbit.anaylsis.converter;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Device;
import edu.crimpbit.service.ConnectorService;
import javafx.util.StringConverter;

import java.util.NoSuchElementException;

/**
 * {@link StringConverter} implementation for {@link Device} values.
 */
public class DeviceStringConverter extends StringConverter<Device> {

    private final ConnectorService connectorService;

    private final ArmStringConverter armStringConverter;

    public DeviceStringConverter(ConnectorService connectorService, ArmStringConverter armStringConverter) {
        this.connectorService = connectorService;
        this.armStringConverter = armStringConverter;

    }

    @Override
    public String toString(Device device) {
        return armStringConverter.toString(device.getArm());
    }

    @Override
    public Device fromString(String string) {
        Arm arm = armStringConverter.fromString(string);
        return connectorService.getDevices().stream()
                .filter(device -> device.getArm() == arm).findFirst()
                .orElseThrow(() -> new NoSuchElementException("No device for arm " + arm));
    }


}
