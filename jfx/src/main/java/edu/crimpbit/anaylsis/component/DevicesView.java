package edu.crimpbit.anaylsis.component;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.config.ApplicationConfiguration;
import edu.crimpbit.service.ConnectorService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.FXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ResourceBundle;

@DeclarativeView(id = ApplicationConfiguration.DEVICES_VIEW, name = "",
        resourceBundleLocation = "bundles.languageBundle",
        viewLocation = "/fxml/DevicesView.fxml",
        initialTargetLayoutId = ApplicationConfiguration.TARGET_CONTAINER_LEFT)
public class DevicesView implements FXComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevicesView.class);

    @FXML
    private ListView<Device> devicesList;

    @Autowired
    private ConnectorService connectorService;

    @Resource
    private Context context;

    @Resource
    private ResourceBundle bundle;


    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        if (message.getMessageBody().equals(FXUtil.MessageUtil.INIT)) {
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
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    private ContextMenu createContextMenu() {
        MultipleSelectionModel<Device> selectionModel = devicesList.getSelectionModel();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem pingMenuItem = new MenuItem();
        pingMenuItem.setText(bundle.getString("devices.view.ping"));
        pingMenuItem.setOnAction(event -> connectorService.ping(selectionModel.getSelectedItem()));
        MenuItem armMenuItem = new MenuItem();
        armMenuItem.setText(bundle.getString("devices.view.switch.arm"));
        armMenuItem.setOnAction(event -> {
            Device device = selectionModel.getSelectedItem();
            device.setArm(device.getArm() == Arm.ARM_LEFT ? Arm.ARM_RIGHT : Arm.ARM_LEFT);
        });
        contextMenu.getItems().addAll(pingMenuItem, new SeparatorMenuItem(), armMenuItem);
        return contextMenu;
    }

}
