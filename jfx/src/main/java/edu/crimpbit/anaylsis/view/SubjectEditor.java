package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Subject;
import edu.crimpbit.service.SubjectService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.javafx.FXMLController;

import javax.annotation.PreDestroy;
import java.util.Optional;

@FXMLController
@Scope("prototype")
public class SubjectEditor implements Persistable<Subject>, FXMLController.RootNodeAware<GridPane>, AutoCloseable, Named {

    @FXML
    private TextField subjectName;

    private Optional<Runnable> unbinder = Optional.empty();

    private ReadOnlyStringWrapper titleProperty = new ReadOnlyStringWrapper();

    private ObjectProperty<Subject> subjectProperty = new SimpleObjectProperty<>();

    private ObservableBooleanValue dirtyValue;

    @Autowired
    private SubjectService subjectService;

    private GridPane rootNode;

    @FXML
    private void initialize() {
        dirtyValue = Bindings.createBooleanBinding(() -> false);

        subjectProperty.addListener((event, oldSubject, newSubject) -> {
            bind(newSubject);
        });

        setPersistable(new Subject());
    }

    private void bind(Subject newSubject) {
        unbind();

        JavaBeanStringProperty nameProperty = getProperty(newSubject, "name");
        nameProperty.addListener((observable, oldValue, newValue) -> newSubject.setName(newValue));

        titleProperty.bind(nameProperty);

        subjectName.textProperty().bindBidirectional(nameProperty);

        unbinder = Optional.of(() -> {
            subjectName.textProperty().unbindBidirectional(nameProperty);
        });
    }

    @Override
    public ObservableStringValue nameValue() {
        return Bindings.concat("Subject: ", titleProperty);
    }

    @Override
    public ObservableBooleanValue dirtyValue() {
        return dirtyValue;
    }

    @Override
    public void save() {
        subjectService.save(subjectProperty.get());
    }

    @Override
    public void setPersistable(Subject subject) {
        subjectProperty.set(subject);
    }

    @Override
    public void setRootNode(GridPane rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public GridPane getRootNode() {
        return rootNode;
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        unbind();
    }

    private void unbind() {
        unbinder.ifPresent(Runnable::run);
    }

    private static JavaBeanStringProperty getProperty(Subject subject, String propertyName) {
        try {
            return JavaBeanStringPropertyBuilder.create().bean(subject).beanClass(Subject.class).name(propertyName).build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to obtain name property.", e);
        }
    }

}
