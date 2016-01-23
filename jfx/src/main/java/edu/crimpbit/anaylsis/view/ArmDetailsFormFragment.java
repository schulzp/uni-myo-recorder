package edu.crimpbit.anaylsis.view;

import edu.crimpbit.Subject;
import edu.crimpbit.anaylsis.util.JavaBeanPropertyUtils;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class ArmDetailsFormFragment {

    @FXML
    private Spinner<Double> girthSpinner;

    @Autowired
    private ControlFactory controlFactory;

    @FXML
    private void initialize() {
        girthSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(15, 55));
        girthSpinner.setEditable(true);
    }

    public void bind(Subject.ArmDetails details) {
        JavaBeanProperty<Double> girthProperty = JavaBeanPropertyUtils.getProperty(details, "girth", Double.class);
        girthSpinner.getValueFactory().valueProperty().bindBidirectional(girthProperty);
    }

}
