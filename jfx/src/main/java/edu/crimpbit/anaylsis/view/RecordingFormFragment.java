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
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.TextFields;
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

    @FXML
    private CheckComboBox<String> gestureTags;

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
            SingleSelectionModel<Subject> subjectSelectionModel = subjectSelect.getSelectionModel();
            SingleSelectionModel<Gesture> gestureSelectionModel = gestureSelect.getSelectionModel();
            if (recording.getId() == null) {
                recording.setSubject(subjectSelectionModel.getSelectedItem());
                Gesture gesture = gestureSelectionModel.getSelectedItem();
                recording.setGesture(gesture);
                gesture.setTags(gestureTags.getCheckModel().getCheckedItems());
            } else {
                subjectSelectionModel.select(recording.getSubject());
                gestureSelectionModel.select(recording.getGesture());
                recording.getGesture().getTags().stream().forEach(tag -> gestureTags.getCheckModel().check(tag));
            }
        }
    }

    @FXML
    private void initialize() {
        gestureSelect.getSelectionModel().selectedItemProperty().addListener(gestureListener);
        subjectSelect.getSelectionModel().selectedItemProperty().addListener(subjectListener);
        controlFactory.initializeSubjectComboBox(subjectSelect);
        controlFactory.initializeGestureComboBox(gestureSelect);
        controlFactory.initializeGestureTagsTextField(gestureTags);
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
