package edu.crimpbit.anaylsis.view.control;

import edu.crimpbit.Device;
import edu.crimpbit.Gesture;
import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.converter.DeviceStringConverter;
import edu.crimpbit.anaylsis.converter.GestureStringConverter;
import edu.crimpbit.anaylsis.converter.SubjectStringConverter;
import edu.crimpbit.service.ConnectorService;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.CheckComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Simple control factory.
 */
@Component
public class ControlFactory  {

    @Autowired
    private SubjectStringConverter subjectStringConverter;

    @Autowired
    private GestureStringConverter gestureStringConverter;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private GestureService gestureService;

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

    public void initializeGestureComboBox(ComboBox<Gesture> gestureComboBox) {
        gestureComboBox.setConverter(gestureStringConverter);
        gestureComboBox.setItems(FXCollections.observableList(gestureService.findAll()));
    }

    public void initializeGestureTagsTextField(CheckComboBox<String> gestureTags) {
        List<String> possibleTags = gestureService.getTags();
        gestureTags.getItems().addAll(possibleTags);
    }

}
