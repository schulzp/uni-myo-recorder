package edu.crimpbit.anaylsis.view;

import edu.crimpbit.anaylsis.event.OpenInNewTab;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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

    @FXML
    private MenuBar menuBar;

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
        tab.setText("New Recording");
        tab.setContent(event.getNode());
        tabPane.getTabs().add(tab);
    }

    @PostConstruct
    private void initialize() {
        createMenus();

        DevicesView devicesView = (DevicesView) itemFactory.call(DevicesView.class);
        accordion.getPanes().add(devicesView.getRootNode());
    }

    private void createMenus() {
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac")) {
            menuBar.setUseSystemMenuBar(true);
        }

        final Menu menuFile = new Menu("File");
        menuFile.getItems().add(createNewItem());
        menuBar.getMenus().addAll(menuFile);
    }

    private MenuItem createNewItem() {
        final MenuItem itemNew = new MenuItem("New");
        itemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
        itemNew.setOnAction((event) -> {
            RecorderFragment recorderFragment = (RecorderFragment) itemFactory.call(RecorderFragment.class);
            openInNewTab(new OpenInNewTab(itemNew, recorderFragment.getRootNode()));
        });
        return itemNew;
    }

}
