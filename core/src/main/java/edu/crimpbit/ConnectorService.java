package edu.crimpbit;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service wrapper for connector/hub.
 */
public class ConnectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorService.class);

    private static final String HUB_ID = "edu.crimpbit.analysis";

    private final ObservableList<Device> devices = FXCollections.observableArrayList();
    private final Hub hub = new Hub(HUB_ID);

    {
        hub.addListener(new AbstractDeviceListener() {

            @Override
            public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
                LOGGER.info("{} {} paired", myo, identify(myo));
            }

            @Override
            public void onUnpair(Myo myo, long timestamp) {
                LOGGER.info("{} {} unpaired", myo, identify(myo));

                updateConnected(myo, false);
            }

            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                LOGGER.info("{} {} disconnected", myo, identify(myo));

                updateConnected(myo, false);
            }

            @Override
            public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
                LOGGER.info("{} {} connected", myo, identify(myo));

                updateConnected(myo, true);
            }

            @Override
            public void onRssi(Myo myo, long timestamp, int rssi) {
                LOGGER.info("{} {} got RSSI: {}", myo, identify(myo), rssi);

                updateConnected(myo, true);
            }

            @Override
            public void onBatteryLevelReceived(Myo myo, long timestamp, int level) {
                LOGGER.info("{} {} got batery: {}", myo, identify(myo), level);

                updateConnected(myo, true);
            }

            @Override
            public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) {
                LOGGER.info("{} {} arm sync (timestamp: {}, arm: {}, x-direction: {}, rotation: {}, warm-up-state: {})",
                        myo, identify(myo), timestamp, arm, xDirection, rotation, warmupState);

                findDevice(myo).ifPresent(device -> {
                    device.setArm(arm);
                    device.setConnected(true);
                    device.setXDirection(xDirection);
                });
            }

            @Override
            public void onArmUnsync(Myo myo, long timestamp) {
                LOGGER.info("{} {} arm sync (timestamp: {})", myo, identify(myo));
            }

            @Override
            public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
                LOGGER.info("{} {} warm up  complete (timestamp: {}, warm-up-result: {})",
                        myo, identify(myo), timestamp, warmupResult);
            }

            private String identify(Myo myo) {
                return findDevice(myo).map(Device::getName).orElse("<unknown>");
            }

            private void updateConnected(Myo myo, boolean connected) {
                findDevice(myo).ifPresent(device -> device.setConnected(connected));
            }

        });
    }

    public void ping(Device device) {
        device.getMyo().vibrate(VibrationType.VIBRATION_SHORT);
    }

    public void refresh() {
        Myo myo;
        do  {
            myo = hub.waitForMyo(2000);

            if (myo != null && !findDevice(myo).isPresent()) {
                LOGGER.debug("added {}", myo);
                devices.add(bind(myo));
            }

            for (Device device : devices) {
                device.getMyo().requestRssi();
            }
        } while (myo != null);
    }

    public ObservableList<Device> getDevices() {
        return devices;
    }

    public Optional<Device> findDevice(Myo myo) {
        if (myo != null) {
            for (Device device : devices) {
                if (myo.equals(device.getMyo())) {
                    return Optional.of(device);
                }
            }
        }
        return Optional.empty();
    }

    Device bind(Myo myo) {
        Device device = new Device(myo);
        device.setName(Integer.toString(devices.size()));
        myo.requestBatteryLevel();
        myo.requestRssi();
        return device;
    }

    Hub getHub() {
        return hub;
    }

}


