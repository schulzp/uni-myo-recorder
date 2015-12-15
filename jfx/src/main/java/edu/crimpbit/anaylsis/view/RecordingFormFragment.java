package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class RecordingFormFragment {

    @FXML
    private ComboBox<String> exerciseSelect;

    @FXML
    private ComboBox<String> subjectSelect;

    private Recording recording;

    private final ChangeListener<String> exerciseListener =
            (observable, oldValue, newValue) -> recording.setExercise(newValue);

    private final ChangeListener<String> subjectListener =
            (observable, oldValue, newValue) -> recording.setSubject(newValue);

    public void setRecording(Recording recording) {
        this.recording = recording;

        exerciseSelect.getSelectionModel().select(recording.getExercise());
        subjectSelect.getSelectionModel().select(recording.getSubject());
    }

    @FXML
    private void initialize() {
        exerciseSelect.getSelectionModel().selectedItemProperty().addListener(exerciseListener);
        subjectSelect.getSelectionModel().selectedItemProperty().addListener(subjectListener);
    }

}
