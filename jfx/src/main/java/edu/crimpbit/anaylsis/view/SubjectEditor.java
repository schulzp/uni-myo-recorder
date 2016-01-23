package edu.crimpbit.anaylsis.view;

import com.thalmic.myo.enums.Arm;
import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.converter.GenderStringConverter;
import edu.crimpbit.anaylsis.util.JavaBeanPropertyUtils;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.service.SubjectService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.javafx.FXMLController;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.Optional;

@FXMLController
@Scope("prototype")
public class SubjectEditor implements Persistable<Subject>, FXMLController.RootNodeAware<Parent>, AutoCloseable, Named {

    @FXML
    private TextField subjectName;

    @FXML
    private Spinner<Integer> subjectAge;

    @FXML
    private Pane genderPane;

    @FXML
    private ArmDetailsFormFragment leftArmFragmentController;

    @FXML
    private ArmDetailsFormFragment rightArmFragmentController;

    private Property<Subject.Gender> subjectGender;

    private Optional<Runnable> unbinder = Optional.empty();

    private ReadOnlyStringWrapper titleProperty = new ReadOnlyStringWrapper();

    private ObjectProperty<Subject> subjectProperty = new SimpleObjectProperty<>();

    private ObservableBooleanValue dirtyValue;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ControlFactory controlFactory;

    @Autowired
    private GenderStringConverter genderStringConverter;

    private Parent rootNode;

    @FXML
    private void initialize() {
        dirtyValue = Bindings.createBooleanBinding(() -> false);
        subjectAge.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100));
        subjectAge.setEditable(true);
        subjectProperty.addListener((event, oldSubject, newSubject) -> {
            bind(newSubject);
        });

        controlFactory.asRadioButtons(EnumSet.complementOf(EnumSet.of(Subject.Gender.UNDEFINED)), genderStringConverter, ((genderButtons, genderProperty) -> {
            subjectGender = genderProperty;
            genderPane.getChildren().addAll(genderButtons);
        }));

        setPersistable(new Subject());
    }

    private void bind(Subject newSubject) {
        unbind();

        leftArmFragmentController.bind(newSubject.getArmDetails().get(Arm.ARM_LEFT));
        rightArmFragmentController.bind(newSubject.getArmDetails().get(Arm.ARM_RIGHT));

        JavaBeanProperty<String> nameProperty = JavaBeanPropertyUtils.getProperty(newSubject, "name", String.class);
        JavaBeanProperty<Integer> ageProperty = JavaBeanPropertyUtils.getProperty(newSubject, "age", Integer.class);
        JavaBeanProperty<Subject.Gender> genderProperty = JavaBeanPropertyUtils.getProperty(newSubject, "gender", Subject.Gender.class);

        titleProperty.bind(nameProperty);

        subjectName.textProperty().bindBidirectional(nameProperty);
        subjectAge.getValueFactory().valueProperty().bindBidirectional(ageProperty);
        subjectGender.bindBidirectional(genderProperty);

        unbinder = Optional.of(() -> {
            subjectName.textProperty().unbindBidirectional(nameProperty);
            subjectAge.getValueFactory().valueProperty().unbindBidirectional(ageProperty);
            subjectGender.unbindBidirectional(genderProperty);
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
    public void setRootNode(Parent rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public Parent getRootNode() {
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

}
