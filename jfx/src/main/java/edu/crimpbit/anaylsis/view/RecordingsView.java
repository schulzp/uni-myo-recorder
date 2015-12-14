package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recording;
import edu.crimpbit.service.RecordingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RecordingsView {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingsView.class);

    @FXML
    private ListView<Recording> recordingsList;

    @Autowired
    private RecordingService recordingService;

    @FXML
    private void initialize() {
        refresh();
    }

    private void refresh() {
        recordingsList.setItems(FXCollections.observableList(recordingService.findAll()));
    }

}
