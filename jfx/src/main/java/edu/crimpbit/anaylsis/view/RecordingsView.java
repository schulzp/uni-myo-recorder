package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.command.CommandService;
import edu.crimpbit.anaylsis.command.OpenCommand;
import edu.crimpbit.service.RecordingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class RecordingsView {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingsView.class);

    @FXML
    private TableView<Recording> recordingsTable;

    @Autowired
    private CommandService commandService;

    @Autowired
    private RecordingService recordingService;

    @FXML
    private void initialize() {
        recordingsTable.setRowFactory(view -> {
            TableRow<Recording> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                OpenCommand command = commandService.getCommand(OpenCommand.class);
                command.setElement(row.getItem());
                commandService.execute(command);
            });
            return row ;
        });
        refresh();
    }

    @EventListener(condition = "#recording.id != null")
    private void onUpdate(Recording recording) {
        refresh();
    }

    private void refresh() {
        recordingsTable.setItems(FXCollections.observableList(recordingService.findAll()));
    }

}
