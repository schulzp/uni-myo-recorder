package edu.crimpbit.anaylsis.view;

import com.thalmic.myo.*;
import edu.crimpbit.service.ConnectorService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.NonInvertibleTransformException;
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
public class ImuView implements FXMLController.RootNodeAware<VBox> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImuView.class);

    @Autowired
    private ConnectorService connectorService;

    @FXML
    private SubScene scene;

    @FXML
    private PerspectiveCamera camera;

    @FXML
    private Box box;

    @FXML
    private Button reset;

    private Rotate resetRotate = new Rotate();
    private Rotate imuRotate = new Rotate();

    private Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
    private Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);

    private VBox rootNode;

    private final AtomicReference<Quaternion> rotation = new AtomicReference<>();

    private final DeviceListener imuListener = new AbstractDeviceListener() {

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            ImuView.this.rotation.lazySet(rotation);
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
//            if (counter++ == 200) {
//                LOGGER.debug("gyro: x: {} y: {} z: {}", gyro.getX(), gyro.getY(), gyro.getZ());
//                counter = 0;
//            }
        }
    };

    @PostConstruct
    private void initialize() {
        camera.getTransforms().addAll(new Translate(0, 0, -15));
        box.translateXProperty().bind(Bindings.multiply(0.5, scene.widthProperty()));
        box.translateYProperty().bind(Bindings.multiply(0.5, scene.heightProperty()));
        box.setMaterial(new PhongMaterial(Color.RED));
        box.getTransforms().addAll(resetRotate, xRotate, yRotate, zRotate);

        reset.setOnAction(event -> {
            Rotate rotate = new Rotate();
            updateRotation(rotate);
            try {
                rotate = (Rotate) rotate.createInverse();
                resetRotate.setAxis(rotate.getAxis());
                resetRotate.setAngle(rotate.getAngle());
            } catch (NonInvertibleTransformException e) {
                LOGGER.warn("Failed to reset", e);
            }
        });

        connectorService.getHub().addListener(imuListener);

        imuRotate.setAxis(Rotate.Z_AXIS);

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(2), event -> {
            updateRotation(imuRotate);
        }));

        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    int counter = 0;

    private void updateRotation(Rotate rotate) {
        Quaternion rotation = this.rotation.get();

        if (rotation == null) {
            return;
        }

        double angle = Math.acos(rotation.getW()) * 2;
        double divisor = Math.sin(angle / 2);
        rotate.setAngle(angle / Math.PI * 360);
        rotate.setAxis(new Point3D(
                rotation.getX() / divisor,
                rotation.getY() / divisor,
                rotation.getZ() / divisor));

        if (counter++ == 10) {
//            LOGGER.debug("Rotate: {} x: {} y: {} z: {}",
//                    rotate.getAngle(),
//                    rotate.getAxis().getX(),
//                    rotate.getAxis().getY(),
//                    rotate.getAxis().getZ()
//            );

            // Calculate Euler angles (roll, pitch, and yaw) from the unit quaternion.
            double roll = Math.atan2(
                    2.0f * (rotation.getW() * rotation.getX() + rotation.getY() * rotation.getZ()),
                    1.0f - 2.0f * (rotation.getX() * rotation.getX() + rotation.getY()) * rotation.getY());

            double pitch = Math.asin(Math.max(-1.0f, Math.min(1.0f, 2.0f * (rotation.getW() * rotation.getY() - rotation.getZ() * rotation.getX()))));

            double yaw = Math.atan2(
                    2.0f * (rotation.getW() * rotation.getZ() + rotation.getX() * rotation.getY()),
                    1.0f - 2.0f * (rotation.getY() * rotation.getY() + rotation.getZ() * rotation.getZ()));

            //LOGGER.debug("roll: {} pitch: {} yaw: {}", roll, pitch, yaw);

            xRotate.setAngle(pitch / Math.PI * 180);
            //yRotate.setAngle(yaw / Math.PI * 180);
            zRotate.setAngle(-roll / Math.PI * 180);

            counter = 0;
        }
    }

    @Override
    public void setRootNode(VBox rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public VBox getRootNode() {
        return rootNode;
    }
}
