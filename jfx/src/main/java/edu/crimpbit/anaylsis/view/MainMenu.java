package edu.crimpbit.anaylsis.view;

import com.google.common.base.CaseFormat;
import edu.crimpbit.anaylsis.command.CommandService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainMenu {

    @Autowired
    private CommandService commandService;

    @FXML
    public void handle(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        String commandId = CaseFormat.LOWER_CAMEL.to(
                CaseFormat.LOWER_UNDERSCORE, source.getId()).replace('_', '.') + ".command";
        commandService.execute(commandId);
    }

}
