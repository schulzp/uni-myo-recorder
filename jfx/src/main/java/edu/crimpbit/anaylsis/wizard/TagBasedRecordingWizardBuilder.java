package edu.crimpbit.anaylsis.wizard;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import edu.crimpbit.*;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.service.RecordingService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.commons.logging.LogFactory;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by peter on 23/01/16.
 */
public class TagBasedRecordingWizardBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger("RecordingWizard");

    private final MessageSourceAccessor messageSourceAccessor;
    private final RecordingService recordingService;
    private final ControlFactory controlFactory;

    private List<String> tags;
    private List<Gesture> gestures;
    private Window owner;
    private String title;
    private Optional<Consumer<Recording>> recordingConsumer = Optional.empty();
    private Executor executor = Executors.newFixedThreadPool(1);

    public TagBasedRecordingWizardBuilder(MessageSourceAccessor messageSourceAccessor, RecordingService recordingService, ControlFactory controlFactory) {
        this.messageSourceAccessor = messageSourceAccessor;
        this.recordingService = recordingService;
        this.controlFactory = controlFactory;
    }

    public TagBasedRecordingWizardBuilder tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public TagBasedRecordingWizardBuilder owner(Window owner) {
        this.owner = owner;
        return this;
    }

    public TagBasedRecordingWizardBuilder gestures(List<Gesture> gestures) {
        this.gestures = gestures;
        return this;
    }

    public TagBasedRecordingWizardBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TagBasedRecordingWizardBuilder consumer(Consumer<Recording> recordingConsumer) {
        this.recordingConsumer = Optional.of(recordingConsumer);
        return this;
    }

    public Wizard build() {
        return createWizard();
    }

    private Wizard createWizard() {
        Objects.requireNonNull(tags, "No tags have been set");
        Objects.requireNonNull(owner, "No owner has been set");
        Objects.requireNonNull(gestures, "No gestures have been set");
        Wizard wizard = new Wizard(owner);
        wizard.setTitle(title);
        wizard.setFlow(createWizardFlow(createWizardPanes(wizard)));
        return wizard;
    }

    private Wizard.Flow createWizardFlow(Collection<WizardPane> wizardPanes) {
        return new Wizard.LinearFlow(wizardPanes);
    }

    private Collection<WizardPane> createWizardPanes(Wizard wizard) {
        ArrayList<WizardPane> wizardPanes = new ArrayList<>();
        wizardPanes.add(createStartWizardPane(wizard));
        tags.stream().map(tag -> createTagWizardPane(wizard, tag)).forEach(wizardPanes::add);
        return wizardPanes;
    }

    private WizardPane createTagWizardPane(Wizard wizard, String tag) {
        WizardPane wizardPane = new WizardPane();
        wizardPane.setHeaderText(messageSourceAccessor.getMessage("recording.wizard.tag.header", new Object[]{ tag }));
        wizardPane.setContent(createRecorders(wizard, tag));
        return wizardPane;
    }

    private Node createRecorders(Wizard wizard, String tag) {
        VBox layout = new VBox();
        gestures.stream().map(gestrue -> {
            String recordButtonText = messageSourceAccessor.getMessage("recorder.record") + ": " + gestrue.getName();
            Button recordButton = new Button(recordButtonText);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                recordButton.setText(recordButtonText);
                recordButton.setDisable(false);
                recordButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CHECK));

                Recorder recorder = (Recorder) recordButton.getUserData();

                recorder.stopAsync().addListener(new Service.Listener() {

                    @Override
                    public void terminated(Service.State from) {
                        Recording recording = recorder.getRecording();
                        LOGGER.debug("Recorder Stopped: {}", recording);
                        recordingService.save(recording);
                        recordingConsumer.ifPresent(consumer -> consumer.accept(recording));
                    }

                }, executor);
            }));
            recordButton.setOnAction(event -> {
                recordButton.setText(messageSourceAccessor.getMessage("recorder.recording"));
                recordButton.setDisable(true);

                Recorder recorder = createRecorder(wizard, gestrue, tag);

                recordButton.setUserData(recorder);

                recorder.startAsync().addListener(new Service.Listener() {

                    @Override
                    public void running() {
                        LOGGER.debug("Recorder Started");
                        timeline.playFromStart();
                    }

                }, executor);
            });
            return recordButton;
        }).forEach(layout.getChildren()::add);
        return layout;
    }

    private Recorder createRecorder(Wizard wizard, Gesture gestrue, String tag) {
        Recorder recorder = recordingService.createRecorder((Device) wizard.getSettings().get("device"));
        gestrue.setTags(Collections.singletonList(tag));
        recorder.getRecording().setGesture(gestrue);
        recorder.getRecording().setSubject((Subject) wizard.getSettings().get("subject"));
        return recorder;
    }

    private WizardPane createStartWizardPane(Wizard wizard) {
        WizardPane wizardPane = new WizardPane();
        wizardPane.setHeaderText(messageSourceAccessor.getMessage("recording.wizard.start.header"));
        ComboBox<Subject> subjectComboBox = new ComboBox<>();
        controlFactory.initializeSubjectComboBox(subjectComboBox);

        ComboBox<Device> deviceComboBox = new ComboBox<>();
        controlFactory.initializeDeviceComboBox(deviceComboBox);

        GridPane layout = new GridPane();
        layout.add(new Text(messageSourceAccessor.getMessage("recording.subject")), 0, 0);
        layout.add(subjectComboBox, 0, 1);
        layout.add(new Text(messageSourceAccessor.getMessage("recorder.device")), 1, 0);
        layout.add(deviceComboBox, 1, 1);

        wizardPane.setContent(layout);

        subjectComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            wizard.getSettings().put("subject", newValue);
        });

        deviceComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            wizard.getSettings().put("device", newValue);
        });

        return wizardPane;
    }

}
