package edu.crimpbit.anaylsis.component;

import com.google.common.util.concurrent.MoreExecutors;
import edu.crimpbit.ConnectorService;
import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.config.BasicConfig;
import javafx.concurrent.Task;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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

    private Executor executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("devices");
        return thread;
    });

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
        return null;
    }


}
