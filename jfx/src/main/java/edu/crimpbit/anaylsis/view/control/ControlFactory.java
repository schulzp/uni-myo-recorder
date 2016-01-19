package edu.crimpbit.anaylsis.view.control;

import edu.crimpbit.Device;
import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.converter.DeviceStringConverter;
import edu.crimpbit.anaylsis.converter.SubjectStringConverter;
import edu.crimpbit.service.ConnectorService;
import edu.crimpbit.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple control factory.
 */
public class ControlFactory  {

    @Autowired
    private SubjectStringConverter subjectStringConverter;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private DeviceStringConverter deviceStringConverter;

    @Autowired
    private ConnectorService connectorService;

    public ComboBox<Device> initializeDeviceComboBox(ComboBox<Device> deviceComboBox) {
        deviceComboBox.setConverter(deviceStringConverter);
        deviceComboBox.setItems(connectorService.getDevices());
        return deviceComboBox;
    }

    public void initializeSubjectComboBox(ComboBox<Subject> subjectComboBox) {
        subjectComboBox.setConverter(subjectStringConverter);
        subjectComboBox.setItems(FXCollections.observableList(subjectService.findAll()));
    }

}
