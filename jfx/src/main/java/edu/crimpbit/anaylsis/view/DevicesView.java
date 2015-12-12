package edu.crimpbit.anaylsis.view;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Device;
import edu.crimpbit.service.ConnectorService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.javafx.FXMLController;

import javax.annotation.PostConstruct;

@FXMLController
public class DevicesView implements FXMLController.RootNodeAware<TitledPane> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevicesView.class);

    private TitledPane rootNode;

    @FXML
    private ListView<Device> devicesList;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Override
    public void setRootNode(TitledPane rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public TitledPane getRootNode() {
        return rootNode;
    }

    @PostConstruct
    private void initialize() {
        devicesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        devicesList.setItems(connectorService.getDevices());

        devicesList.setContextMenu(createContextMenu());
        devicesList.setCellFactory(list -> new ListCell<Device>() {

            private CheckBox checkBox = new CheckBox();
            private BooleanProperty checkedProperty;
            private ChangeListener<Arm> armListener;

            @Override
            public void updateItem(Device device, boolean empty) {
                super.updateItem(device, empty);

                textProperty().unbind();
                if (checkedProperty != null) {
                    checkBox.selectedProperty().unbindBidirectional(checkedProperty);
                }

                if (!empty) {
                    setGraphic(checkBox);

                    if (armListener != null) {
                        device.armProperty().removeListener(armListener);
                    }
                    armListener = (property, oldValue, newValue) -> {
                        Platform.runLater(() -> setText(newValue.name()));
                    };

                    device.armProperty().addListener(armListener);
                    setText(device.getArm().name());

                    checkedProperty = device.selectedProperty();
                    checkBox.selectedProperty().bindBidirectional(checkedProperty);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }

        });
    }

    private ContextMenu createContextMenu() {
        MultipleSelectionModel<Device> selectionModel = devicesList.getSelectionModel();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem pingMenuItem = new MenuItem();
        pingMenuItem.setText(messageSourceAccessor.getMessage("devices.view.ping"));
        pingMenuItem.setOnAction(event -> connectorService.ping(selectionModel.getSelectedItem()));
        MenuItem armMenuItem = new MenuItem();
        armMenuItem.setText(messageSourceAccessor.getMessage("devices.view.switch.arm"));
        armMenuItem.setOnAction(event -> {
            Device device = selectionModel.getSelectedItem();
            device.setArm(device.getArm() == Arm.ARM_LEFT ? Arm.ARM_RIGHT : Arm.ARM_LEFT);
        });
        contextMenu.getItems().addAll(pingMenuItem, new SeparatorMenuItem(), armMenuItem);
        return contextMenu;
    }
}
