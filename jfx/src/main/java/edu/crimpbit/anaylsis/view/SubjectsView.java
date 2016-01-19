package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.command.CommandService;
import edu.crimpbit.anaylsis.command.OpenControllerCommandFactory;
import edu.crimpbit.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class SubjectsView {

    @FXML
    private ListView<Subject> subjectList;

    @FXML
    private MenuItem newSubjectItem;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private OpenControllerCommandFactory openControllerCommandFactory;

    @FXML
    private void initialize() {
        subjectList.setCellFactory(view -> {
            ListCell<Subject> cell = new ListCell<Subject>() {

                @Override
                protected void updateItem(Subject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.getName());
                    } else {
                        setText(null);
                    }
                }

            };

            cell.setOnMouseClicked(event -> {
                if (cell.getItem() != null && !cell.isEmpty()) {
                    commandService.execute(openControllerCommandFactory.create(cell.getItem()));
                }
            });

            return cell;
        });

        newSubjectItem.setOnAction(event -> {
            commandService.execute("file.new.subject.command");
        });

        refresh();
    }

    @EventListener(condition = "#subject.id != null")
    private void onUpdate(Subject subject) {
        refresh();
    }

    private void refresh() {
        subjectList.setItems(FXCollections.observableList(subjectService.findAll()));
    }

}
