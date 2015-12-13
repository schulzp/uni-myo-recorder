package edu.crimpbit.anaylsis.view;

import edu.crimpbit.anaylsis.event.OpenInNewTab;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.javafx.FXMLController;
import org.springframework.javafx.FXMLControllerFactory;

import javax.annotation.PostConstruct;

@FXMLController
public class MainLayout implements FXMLController.RootNodeAware<BorderPane> {

    private BorderPane parent;

    @Autowired
    private FXMLControllerFactory itemFactory;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @FXML
    private Accordion accordion;

    @FXML
    private TabPane tabPane;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Override
    public BorderPane getRootNode() {
        return parent;
    }

    @Override
    public void setRootNode(BorderPane rootNode) {
        this.parent = rootNode;
    }

    @EventListener
    public void openInNewTab(OpenInNewTab event) {
        Tab tab = new Tab();
        tab.textProperty().bind(event.textProperty());
        tab.setContent(event.getNode());
        tabPane.getTabs().add(tab);
    }

    @PostConstruct
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(0));
    }

}
