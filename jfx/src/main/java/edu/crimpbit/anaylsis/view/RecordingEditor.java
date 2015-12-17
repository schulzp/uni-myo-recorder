package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import edu.crimpbit.service.RecordingService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.javafx.FXMLController;

@FXMLController
@Scope("prototype")
public class RecordingEditor implements FXMLController.RootNodeAware<BorderPane>, Persistable<Recording> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingEditor.class);
    private static final String STYLE_CLASS_VALIDATION_FAILED = "validation-failed";

    private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper("New Recording");

    private final ObjectProperty<Recording> recording = new SimpleObjectProperty<>();

    private BorderPane rootNode;

    @FXML
    private RecorderFragment recorderFragmentController;

    @FXML
    private RecordingFormFragment recordingFormFragmentController;

    @FXML
    private RecordingChartsFragment recordingChartsFragmentController;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setRootNode(BorderPane rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public BorderPane getRootNode() {
        return rootNode;
    }

    public ObjectProperty<Recording> recordingProperty() {
        return recording;
    }

    public void setRecording(Recording recording) {
        this.recording.unbind();
        this.recording.set(recording);
    }

    @Override
    public void save() {
        applicationEventPublisher.publishEvent(new Task<Recording>() {

            @Override
            protected Recording call() throws Exception {
                Recording recording = RecordingEditor.this.recording.get();
                recordingService.save(recording);
                return recording;
            }

            @Override
            protected void succeeded() {
                rootNode.getStyleClass().remove(STYLE_CLASS_VALIDATION_FAILED);
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                rootNode.getStyleClass().add(STYLE_CLASS_VALIDATION_FAILED);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(exception.getLocalizedMessage());
                alert.show();
            }

        });
    }

    @Override
    public ReadOnlyStringProperty textProperty() {
        return text;
    }

    @FXML
    private void initialize() {
        recorderFragmentController.currentlyRecordingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                recordingChartsFragmentController.startLiveUpdate();
            } else {
                recordingChartsFragmentController.stopLiveUpdate();
            }
        });
        recording.bind(recorderFragmentController.recordingProperty());
        recording.addListener(((observable, oldValue, newValue) -> {
            recordingFormFragmentController.setRecording(newValue);
            recordingChartsFragmentController.addEmgData(newValue.getEmgData());
        }));
    }

}
