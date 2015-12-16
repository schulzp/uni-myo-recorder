package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tooltip;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Iterator;

@Controller
@Scope("prototype")
public class RecordingFormFragment {

    private static final String STYLE_CLASS_CONSTRAINT_VIOLATION = "constraint-violation";

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

    public void handleException(ConstraintViolationException exception) {
        ((ConstraintViolationException) exception).getConstraintViolations().stream()
                .forEach(this::handleViolation);
    }

    private void handleViolation(ConstraintViolation<?> violation) {
        Iterator<Path.Node> pathIterator = violation.getPropertyPath().iterator();
        String name = null;
        while (pathIterator.hasNext()) {
            name = pathIterator.next().getName();
        }
        markConstraintViolation(subjectSelect, null);
        markConstraintViolation(exerciseSelect, null);
        if ("subject".equals(name)) {
            markConstraintViolation(subjectSelect, violation);
        } else if ("exercise".equals(name)) {
            markConstraintViolation(exerciseSelect, violation);
        }

    }

    private void markConstraintViolation(Node node, ConstraintViolation<?> violation) {
        ObservableList<String> styleClass = node.getStyleClass();
        styleClass.remove(STYLE_CLASS_CONSTRAINT_VIOLATION);
        if (violation != null) {
            styleClass.add(STYLE_CLASS_CONSTRAINT_VIOLATION);
        }
    }
}
