package edu.crimpbit.anaylsis.view;

import edu.crimpbit.anaylsis.command.FileSaveCommand;
import edu.crimpbit.anaylsis.command.OpenControllerCommand;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.javafx.FXMLController;
import org.springframework.javafx.FXMLControllerFactory;

import javax.annotation.PostConstruct;

@FXMLController
public class MainLayout implements FXMLController.RootNodeAware<BorderPane> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FXMLControllerFactory controllerFactory;

    @FXML
    private Accordion accordion;

    @FXML
    private TabPane tabPane;

    private BorderPane parent;

    @Override
    public BorderPane getRootNode() {
        return parent;
    }

    @Override
    public void setRootNode(BorderPane rootNode) {
        this.parent = rootNode;
    }

    @EventListener
    public void open(OpenControllerCommand command) {
        Tab tab = new Tab();
        tab.setOnClosed(event -> close(tab));
        tabPane.getTabs().add(bindTab(tab, command.createController()));
        tabPane.getSelectionModel().select(tab);
    }

    private void close(Tab tab) {
        Object content = tab.getUserData();
        if (content instanceof AutoCloseable) {
            try {
                ((AutoCloseable) content).close();
            } catch (Exception e) {
                LOGGER.error("Failed to close tab.", e);
            }
        }
    }

    @EventListener(classes = FileSaveCommand.class)
    public void save() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            Object content = tab.getUserData();
            if (content instanceof Persistable) {
                ((Persistable) content).save();
            }
        }
    }

    private Tab bindTab(Tab tab, Object content) {
        if (content instanceof FXMLController.RootNodeAware) {
            tab.setContent(((FXMLController.RootNodeAware) content).getRootNode());
        }
        if (content instanceof Named) {
            tab.textProperty().bind(((Named) content).nameValue());
        }

        tab.setUserData(content);
        return tab;
    }

    @PostConstruct
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(1));
    }

}
