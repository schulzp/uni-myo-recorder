package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@Scope("prototype")
public class RecordingFormFragment {

    @FXML
    private ComboBox<String> exerciseSelect;

    @FXML
    private ComboBox<String> subjectSelect;

    private Recording recording;

    private final ChangeListener<String> exerciseListener =
            (selection, oldExercise, newExercise) -> {
                if (recording != null) {
                    recording.setExercise(newExercise);
                }
            };

    private final ChangeListener<String> subjectListener =
            (selection, oldSubject, newSubject) -> {
                if (recording != null) {
                    recording.setSubject(newSubject);
                }
            };

    public void setRecording(Recording recording) {
        this.recording = recording;

        if (recording != null) {
            SingleSelectionModel<String> exerciseSelectionModel = exerciseSelect.getSelectionModel();
            SingleSelectionModel<String> subjectSelectionModel = subjectSelect.getSelectionModel();
            if (recording.getId() == null) {
                recording.setExercise(exerciseSelectionModel.getSelectedItem());
                recording.setSubject(subjectSelectionModel.getSelectedItem());
            } else {
                exerciseSelectionModel.select(recording.getExercise());
                subjectSelectionModel.select(recording.getSubject());
            }
        }
    }

    @FXML
    private void initialize() {
        exerciseSelect.getSelectionModel().selectedItemProperty().addListener(exerciseListener);
        subjectSelect.getSelectionModel().selectedItemProperty().addListener(subjectListener);
    }

}
