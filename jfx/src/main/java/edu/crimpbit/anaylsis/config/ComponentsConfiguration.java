package edu.crimpbit.anaylsis.config;

import edu.crimpbit.anaylsis.util.DeviceStringConverter;
import edu.crimpbit.anaylsis.view.control.DeviceComboBox;
import edu.crimpbit.service.ConnectorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ComponentsConfiguration {

    @Bean
    @Scope("prototype")
    public DeviceComboBox deviceComboBox(ConnectorService connectorService, DeviceStringConverter deviceStringConverter) {
        DeviceComboBox deviceComboBox = new DeviceComboBox();
        deviceComboBox.setConverter(deviceStringConverter);
        deviceComboBox.setItems(connectorService.getDevices());
        return deviceComboBox;
    }

}
