package edu.crimpbit.anaylsis.event;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;
import org.springframework.context.ApplicationEvent;

public class OpenInNewTab extends ApplicationEvent {

    private final Node node;
    private final ReadOnlyStringProperty text;

    public OpenInNewTab(Object source, Node node, ReadOnlyStringProperty text) {
        super(source);
        this.node = node;
        this.text = text;
    }

    public Node getNode() {
        return node;
    }

    public ReadOnlyStringProperty textProperty() {
        return text;
    }

}
