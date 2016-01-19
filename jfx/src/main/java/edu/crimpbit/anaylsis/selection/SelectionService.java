package edu.crimpbit.anaylsis.selection;

import com.sun.scenario.effect.impl.sw.sse.SSERendererDelegate;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selection service.
 */
public class SelectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectionService.class);

    private final ObservableList<Object> selection = FXCollections.observableArrayList();

    private ReadOnlyObjectWrapper<SelectionModel<?>> selectionModel = new ReadOnlyObjectWrapper<>();

    public SelectionService() {
        selectionModel.addListener((observable, oldValue, newValue) -> {
            LOGGER.info("Selection model changed from {} to {}", oldValue, newValue);
        });
    }

    public ReadOnlyObjectProperty<SelectionModel<?>> selectionModelProperty() {
        return selectionModel;
    }

    public void register(ObservableBooleanValue focused, SelectionModel<?> selectionModel) {
        focused.addListener((observable, oldValue, newValue) -> setSelectionModel(observable, selectionModel));
        setSelectionModel(focused, selectionModel);
        bind(selectionModel);
    }

    private void bind(SelectionModel<?> selectionModel) {
        if (selectionModel instanceof MultipleSelectionModel) {
            ((MultipleSelectionModel) selectionModel).getSelectedItems().addListener((ListChangeListener) c -> {
               updateSelection(selectionModel);
            });
        } else {
            selectionModel.selectedItemProperty().addListener((observable, oldItem, newItem) -> {
                updateSelection(selectionModel);
            });
        }
    }

    private void updateSelection(SelectionModel otherSelectionModel) {
        if (isCurrentSelectionModel(otherSelectionModel)) {
            selection.clear();
            if (otherSelectionModel instanceof MultipleSelectionModel) {
                selection.addAll(((MultipleSelectionModel) otherSelectionModel).getSelectedItems());
            } else {
                selection.add(otherSelectionModel.getSelectedItem());
            }
            LOGGER.debug("Selection changed: {}", selection);
        }
    }

    public ObservableList<Object> getSelection() {
        return selection;
    }

    private boolean isCurrentSelectionModel(SelectionModel<?> otherSelectionModel) {
        return otherSelectionModel == selectionModel.get();
    }

    private void setSelectionModel(ObservableValue<? extends Boolean> focused, SelectionModel<?> newSelectionModel) {
        if (focused.getValue()) {
            selectionModel.set(newSelectionModel);
            updateSelection(newSelectionModel);
        }
    }

}
