package edu.crimpbit.anaylsis.command;

import edu.crimpbit.anaylsis.selection.SelectionService;
import edu.crimpbit.anaylsis.view.control.AlertFactory;
import edu.crimpbit.repository.RepositoryProvider;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

/**
 * Global delete command.
 */
public class FileDeleteCommand implements Command {

    private final SelectionService selectionService;

    private final ObservableBooleanValue executable;

    private final RepositoryProvider repositoryProvider;

    private final AlertFactory alertFactory;

    public FileDeleteCommand(SelectionService selectionService, RepositoryProvider repositoryProvider, AlertFactory alertFactory) {
        this.selectionService = selectionService;
        this.repositoryProvider = repositoryProvider;
        this.alertFactory = alertFactory;

        executable = Bindings.greaterThan(Bindings.size(selectionService.getSelection()), 0);
    }

    public ObservableBooleanValue executableProperty() {
        return executable;
    }

    @Override
    public void run() {
        ArrayList<Object> selection = new ArrayList<>(selectionService.getSelection());

        Alert alert = alertFactory.confirm("file.delete.confirm");

        alert.resultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == ButtonType.OK) {
                selection.stream().forEach(selected -> repositoryProvider.get(selected).delete(selected));
            }
        });

        alert.show();

    }

}
