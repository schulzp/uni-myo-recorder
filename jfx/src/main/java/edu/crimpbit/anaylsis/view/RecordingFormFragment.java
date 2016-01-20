package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Gesture;
import edu.crimpbit.Recording;
import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ComboBox<Gesture> gestureSelect;

    @FXML
    private ComboBox<Subject> subjectSelect;

    @Autowired
    private ControlFactory controlFactory;

    private Recording recording;

    private final ChangeListener<Gesture> gestureListener =
            (selection, oldExercise, newExercise) -> {
                if (recording != null) {
                    recording.setGesture(newExercise);
                }
            };

    private final ChangeListener<Subject> subjectListener =
            (selection, oldSubject, newSubject) -> {
                if (recording != null) {
                    recording.setSubject(newSubject);
                }
            };

    public void setRecording(Recording recording) {
        this.recording = recording;

        if (recording != null) {
            SingleSelectionModel<Gesture> exerciseSelectionModel = gestureSelect.getSelectionModel();
            SingleSelectionModel<Subject> subjectSelectionModel = subjectSelect.getSelectionModel();
            if (recording.getId() == null) {
                recording.setGesture(exerciseSelectionModel.getSelectedItem());
                recording.setSubject(subjectSelectionModel.getSelectedItem());
            } else {
                exerciseSelectionModel.select(recording.getGesture());
                subjectSelectionModel.select(recording.getSubject());
            }
        }
    }

    @FXML
    private void initialize() {
        gestureSelect.getSelectionModel().selectedItemProperty().addListener(gestureListener);
        subjectSelect.getSelectionModel().selectedItemProperty().addListener(subjectListener);
        controlFactory.initializeSubjectComboBox(subjectSelect);
        controlFactory.initializeGestureComboBox(gestureSelect);
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
        markConstraintViolation(gestureSelect, null);
        if ("subject".equals(name)) {
            markConstraintViolation(subjectSelect, violation);
        } else if ("exercise".equals(name)) {
            markConstraintViolation(gestureSelect, violation);
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
