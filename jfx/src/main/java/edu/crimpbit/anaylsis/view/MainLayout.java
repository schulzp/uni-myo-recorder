package edu.crimpbit.anaylsis.view;

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
import org.springframework.javafx.FXMLController;
import org.springframework.javafx.FXMLControllerFactory;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@FXMLController
public class MainLayout implements FXMLController.RootNodeAware<BorderPane> {

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
    public void open(OpenCommand command) {
        Tab tab = new Tab();
        tabPane.getTabs().add(bindTab(tab, getContent(command)));
        tabPane.getSelectionModel().select(tab);
    }

    private static final List<Class<?>> controllerClasses = Arrays.asList(
        RecordingEditor.class, ImuView.class
    );

    private Object getContent(OpenCommand command) {
        Object content = command.getContent();
        if (content instanceof Class && controllerClasses.contains(content)) {
            content = controllerFactory.call((Class<?>) content);
        } else if (content instanceof Recording) {
            Recording recording = (Recording) content;
            content = controllerFactory.call(RecordingEditor.class);
            ((RecordingEditor) content).setRecording(recording);
        }
        return content;
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

    private Tab bindTab(Tab tab, Object element) {
        if (element instanceof FXMLController.RootNodeAware) {
            tab.setContent(((FXMLController.RootNodeAware) element).getRootNode());
        }
        if (element instanceof Persistable) {
            tab.textProperty().bind(((Persistable) element).textProperty());
        }
        tab.setUserData(element);
        return tab;
    }

    @PostConstruct
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(1));
    }

}
