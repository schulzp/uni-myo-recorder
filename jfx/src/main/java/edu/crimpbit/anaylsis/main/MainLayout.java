package edu.crimpbit.anaylsis.main;

import javafx.scene.control.SplitPane;
import org.springframework.javafx.FXMLComponent;

/**
 * Created by schulz on 09.12.2015.
 */
@FXMLComponent
public class MainLayout implements FXMLComponent.RootNodeAware<SplitPane> {

    private SplitPane parent;

    @Override
    public SplitPane getRootNode() {
        return parent;
    }

    @Override
    public void setRootNode(SplitPane parent) {
        this.parent = parent;
    }

}
