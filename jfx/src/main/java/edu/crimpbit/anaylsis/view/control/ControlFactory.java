package edu.crimpbit.anaylsis.view.control;

import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.util.DeviceStringConverter;
import edu.crimpbit.service.ConnectorService;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple control factory.
 */
public class ControlFactory  {

    @Autowired
    private DeviceStringConverter deviceStringConverter;

    @Autowired
    private ConnectorService connectorService;

    public ComboBox<Device> initializeDeviceComboBox(ComboBox<Device> deviceComboBox) {
        deviceComboBox.setConverter(deviceStringConverter);
        deviceComboBox.setItems(connectorService.getDevices());
        return deviceComboBox;
    }

}
