package edu.crimpbit.anaylsis.event;

import org.springframework.context.ApplicationEvent;

public class OpenInNewTab extends ApplicationEvent {

    private final Object element;

    public OpenInNewTab(Object source, Object element) {
        super(source);
        this.element = element;
    }

    public Object getElement() {
        return element;
    }

}
