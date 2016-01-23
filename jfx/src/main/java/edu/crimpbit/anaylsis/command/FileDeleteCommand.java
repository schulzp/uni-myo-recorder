package edu.crimpbit.anaylsis.command;

import edu.crimpbit.anaylsis.selection.SelectionService;
import edu.crimpbit.repository.RepositoryProvider;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;

import java.util.ArrayList;

/**
 * Global delete command.
 */
public class FileDeleteCommand implements Command {

    private final SelectionService selectionService;

    private final ObservableBooleanValue executable;

    private final RepositoryProvider repositoryProvider;

    public FileDeleteCommand(SelectionService selectionService, RepositoryProvider repositoryProvider) {
        this.selectionService = selectionService;
        this.repositoryProvider = repositoryProvider;

        executable = Bindings.greaterThan(Bindings.size(selectionService.getSelection()), 0);
    }

    public ObservableBooleanValue executableProperty() {
        return executable;
    }

    @Override
    public void run() {
        new ArrayList<>(selectionService.getSelection())
        .stream().forEach(selected -> repositoryProvider.get(selected).delete(selected));
    }

}
