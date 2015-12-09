package edu.crimpbit.anaylsis.main;

import javafx.scene.control.SplitPane;
import org.springframework.javafx.FXMLComponent;

/**
 * Created by schulz on 09.12.2015.
 */
@FXMLComponent
public class MainLayout implements FXMLComponent.ParentAware<SplitPane> {

    private SplitPane parent;

    @Override
    public SplitPane getParent() {
        return parent;
    }

    @Override
    public void setParent(SplitPane parent) {
        this.parent = parent;
    }

}
