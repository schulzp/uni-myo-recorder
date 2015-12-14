package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Recorder;
import edu.crimpbit.Recording;
import edu.crimpbit.anaylsis.command.FileSaveCommand;
import edu.crimpbit.anaylsis.command.OpenCommand;
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

    @Autowired
    private FXMLControllerFactory controllerFactory;

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
    public void open(OpenCommand command) {
        Object element = command.getElement();

        if (element instanceof Recorder) {
            RecorderFragment fragment = (RecorderFragment) controllerFactory.call(RecorderFragment.class);
            fragment.setRecorder((Recorder) element);
            tabPane.getTabs().add(bindTab(new Tab(), fragment));
        } if (element instanceof Recording) {
            // TODO open tab for recording
        }
    }

    @EventListener(classes = FileSaveCommand.class)
    public void save() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab != null) {
            Object element = tab.getUserData();
            if (element instanceof Persistable) {
                ((Persistable) element).save();
            }
        }
    }

    private Tab bindTab(Tab tab, Persistable<?> element) {
        if (element instanceof FXMLController.RootNodeAware) {
            tab.setContent(((FXMLController.RootNodeAware) element).getRootNode());
        }
        if (element != null) {
            tab.textProperty().bind(((Persistable) element).nameProperty());
        }
        tab.setUserData(element);
        return tab;
    }

    @PostConstruct
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(0));
    }

}
