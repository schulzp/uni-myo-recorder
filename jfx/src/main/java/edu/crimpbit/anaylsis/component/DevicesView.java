package edu.crimpbit.anaylsis.component;

import edu.crimpbit.ConnectorService;
import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.callback.ConnectorCallback;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.FXUtil;
import org.springframework.beans.factory.annotation.Autowired;

@DeclarativeView(id = BasicConfig.DEVICES_VIEW, name = "",
        resourceBundleLocation = "bundles.languageBundle",
        viewLocation = "/fxml/DevicesView.fxml",
        initialTargetLayoutId = BasicConfig.TARGET_CONTAINER_LEFT)
public class DevicesView implements FXComponent {

    @FXML
    private ToggleButton refreshButton;

    @FXML
    private ListView<Device> devicesList;

    @Autowired
    private ConnectorService connectorService;

    @Resource
    private Context context;

    private void refresh(ActionEvent event) {
        context.send(ConnectorCallback.COMMAND_REFRESH);
    }

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        if (message.getMessageBody().equals(FXUtil.MessageUtil.INIT)) {
            devicesList.setItems(connectorService.getDevices());
            refreshButton.selectedProperty().addListener(selected -> {
                if (refreshButton.isSelected()) {
                    refresh();
                }
            });

            refresh();
        }
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        if (message.getMessageBody().equals(ConnectorCallback.COMMAND_REFRESH_DONE)) {
            refreshButton.setSelected(false);
        }
        return null;
    }

    private void refresh() {
        context.send(BasicConfig.CONNECTOR_CALLBACK, ConnectorCallback.COMMAND_REFRESH);
    }

}
