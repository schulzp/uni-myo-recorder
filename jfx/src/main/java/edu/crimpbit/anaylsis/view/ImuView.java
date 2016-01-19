package edu.crimpbit.anaylsis.view;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import edu.crimpbit.Device;
import edu.crimpbit.anaylsis.view.control.ControlFactory;
import edu.crimpbit.service.ConnectorService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javafx.FXMLController;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

@FXMLController
public class ImuView implements FXMLController.RootNodeAware<VBox>, Named  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImuView.class);

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ControlFactory controlFactory;

    @FXML
    private SubScene scene;

    @FXML
    private PerspectiveCamera camera;

    @FXML
    private Box box;

    @FXML
    private Button reset;

    @FXML
    private ComboBox<Device> deviceSelect;

    private Rotate imuRotate = new Rotate();

    private VBox rootNode;

    private final AtomicReference<Quaternion> rotation = new AtomicReference<>();

    private Quaternion baseRotation = null;

    private final DeviceListener imuListener = new AbstractDeviceListener() {

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            if (selectedDevice != null) {
                ImuView.this.rotation.lazySet(rotation);
            }
        }

    };

    private Device selectedDevice;

    @PostConstruct
    private void initialize() {
        camera.getTransforms().addAll(new Translate(0, 0, -15));
        box.translateXProperty().bind(Bindings.multiply(0.5, scene.widthProperty()));
        box.translateYProperty().bind(Bindings.multiply(0.5, scene.heightProperty()));
        box.setMaterial(new PhongMaterial(Color.RED));
        box.getTransforms().addAll(imuRotate);

        reset.setOnAction(event -> {
            Quaternion rotation = this.rotation.get();
            if (rotation != null) {
                baseRotation =  rotation.conjugate();
            }
        });

        controlFactory.initializeDeviceComboBox(deviceSelect);
        deviceSelect.getSelectionModel().selectedItemProperty().addListener((prop, oldValue, newValue) -> {
            selectedDevice = newValue;
        });

        deviceSelect.getSelectionModel().select(0);

        connectorService.getHub().addListener(imuListener);

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(2), event -> {
            updateRotation();
        }));

        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private void updateRotation() {
        Quaternion rotation = this.rotation.get();

        if (rotation == null) {
            return;
        }

        if (baseRotation != null) {
            rotation = baseRotation.multiply(rotation);
        }

        double angle = Math.acos(rotation.getW()) * 2;
        double divisor = Math.sin(angle / 2);
        imuRotate.setAngle(angle / Math.PI * 180);
        imuRotate.setAxis(new Point3D(
                rotation.getY() / divisor,
                -rotation.getZ() / divisor,
                -rotation.getX() / divisor));
    }

    @Override
    public void setRootNode(VBox rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public VBox getRootNode() {
        return rootNode;
    }

    @Override
    public ObservableStringValue nameValue() {
        return new ReadOnlyStringWrapper("IMU");
    }

}
