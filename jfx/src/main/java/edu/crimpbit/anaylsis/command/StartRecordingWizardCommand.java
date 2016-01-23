package edu.crimpbit.anaylsis.command;

import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.scene.SceneHolder;
import edu.crimpbit.anaylsis.view.RecordingEditor;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.anaylsis.wizard.TagBasedRecordingWizardBuilder;
import edu.crimpbit.service.GestureService;
import edu.crimpbit.service.RecordingService;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Created by peter on 23/01/16.
 */
public class StartRecordingWizardCommand implements Command {

    @Autowired
    private OpenControllerCommandFactory openControllerCommandFactory;

    @Autowired
    private CommandService commandService;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private ControlFactory controlFactory;

    @Autowired
    private GestureService gestureService;

    @Autowired
    private SceneHolder sceneHolder;

    @Override
    public void run() {
        OpenControllerCommand<Recording, RecordingEditor> command = openControllerCommandFactory.create(RecordingEditor.class, (Recording) null);
        RecordingEditor controller = command.createController();

        commandService.execute(command);

        new TagBasedRecordingWizardBuilder(messageSourceAccessor, recordingService, controlFactory)
                .owner(sceneHolder.get().get().getWindow())
                .tags(gestureService.getTags())
                .gestures(gestureService.findAll())
                .title(messageSourceAccessor.getMessage("recording.wizard.title"))
                .consumer(recording -> Platform.runLater(() -> controller.setPersistable(recording)))
                .build().showAndWait();
    }

}
