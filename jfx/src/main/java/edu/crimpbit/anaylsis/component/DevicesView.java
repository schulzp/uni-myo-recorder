package edu.crimpbit.anaylsis.component;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.ConnectorService;
import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;
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
import java.util.concurrent.Executor;

@DeclarativeView(id = BasicConfig.DEVICES_VIEW, name = "",
        resourceBundleLocation = "bundles.languageBundle",
        viewLocation = "/fxml/DevicesView.fxml",
        initialTargetLayoutId = BasicConfig.TARGET_CONTAINER_LEFT)
public class DevicesView implements FXComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevicesView.class);

    @FXML
    private ToggleButton refreshButton;

    @FXML
    private ListView<Device> devicesList;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private Executor executor;

    @Resource
    private Context context;

    @Resource
    private ResourceBundle bundle;

    private void refresh() {
        Task<Void> refreshTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                connectorService.refresh();
                refreshButton.setSelected(false);
                return null;
            }

        };

        executor.execute(refreshTask);
    }

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        if (message.getMessageBody().equals(FXUtil.MessageUtil.INIT)) {
            devicesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            devicesList.setItems(connectorService.getDevices());
            refreshButton.selectedProperty().addListener(selected -> {
                if (refreshButton.isSelected()) {
                    refresh();
                }
            });

            devicesList.setContextMenu(createContextMenu());
            devicesList.setCellFactory(list -> new ListCell<Device>() {

                private CheckBox checkBox = new CheckBox();
                private BooleanProperty checkedProperty;

                @Override
                public void updateItem(Device item, boolean empty) {
                    super.updateItem(item, empty);

                    textProperty().unbind();
                    if (checkedProperty != null) {
                        checkBox.selectedProperty().unbindBidirectional(checkedProperty);
                    }

                    if (!empty) {
                        setGraphic(checkBox);

                        textProperty().bind(Bindings.convert(item.armProperty()));
                        checkedProperty = item.selectedProperty();
                        checkBox.selectedProperty().bindBidirectional(checkedProperty);
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }

            });

            refresh();
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
