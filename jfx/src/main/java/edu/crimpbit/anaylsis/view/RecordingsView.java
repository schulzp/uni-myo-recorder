package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.command.CommandService;
import edu.crimpbit.anaylsis.command.OpenControllerCommandFactory;
import edu.crimpbit.anaylsis.selection.SelectionService;
import edu.crimpbit.anaylsis.util.InstanceUtils;
import edu.crimpbit.anaylsis.util.MouseEventUtils;
import edu.crimpbit.service.RecordingService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.controlsfx.control.table.TableFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class RecordingsView {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingsView.class);

    @FXML
    private TableView<Recording> recordingsTable;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private OpenControllerCommandFactory openControllerCommandFactory;

    @Autowired
    private SelectionService selectionService;

    @Autowired
    private ResourceBundle resourceBundle;

    @FXML
    private void initialize() {
        selectionService.register(recordingsTable.focusedProperty(), recordingsTable.getSelectionModel());
        recordingsTable.getColumns().addAll(createColumns());
        recordingsTable.setRowFactory(view -> {
            TableRow<Recording> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (MouseEventUtils.isDoubleClick(event)) {
                    commandService.execute(openControllerCommandFactory.create(row.getItem()));
                }
            });
            return row;
        });
        refresh();

        new TableFilter<Recording>(recordingsTable);
    }

    private List<TableColumn<Recording, ?>> createColumns() {
        return Arrays.asList(
                InstanceUtils.with(new TableColumn<Recording, String>(), subjectColumn -> {
                    subjectColumn.setText(resourceBundle.getString("recording.subject"));
                    subjectColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().getSubject().getName()));
                }),
                InstanceUtils.with(new TableColumn<Recording, String>(), gestureColumn -> {
                    gestureColumn.setText(resourceBundle.getString("recording.gesture"));
                    gestureColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().getGesture().getName()));
                }),
                InstanceUtils.with(new TableColumn<Recording, String>(), tagColumn -> {
                    tagColumn.setText(resourceBundle.getString("recording.gesture.tags"));
                    tagColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().getGesture().getTags().toString()));
                }));
    }

    @FXML
    private void delete(ActionEvent event) {
        commandService.execute("file.delete.command");
    }

    @EventListener(condition = "#event == 'update.recording'")
    private void onUpdate(String event) {
        refresh();
    }

    private void refresh() {
        recordingsTable.setItems(FXCollections.observableList(recordingService.findAll()));
    }

}
