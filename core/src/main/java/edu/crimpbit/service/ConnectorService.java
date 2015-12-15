package edu.crimpbit.service;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.thalmic.myo.*;
import com.thalmic.myo.enums.*;
import edu.crimpbit.Device;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

/**
 * Service wrapper for connector/hub.
 */
@Service
public class ConnectorService extends AbstractExecutionThreadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorService.class);

    private final ObservableList<Device> devices = FXCollections.observableArrayList();

    private final DeviceListener listener = new AbstractDeviceListener() {

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Device device = findOrCreateDevice(myo);
            device.setStatus(Device.Status.DISCONNECTED);
            LOGGER.info("{} disconnected", device);
        }

        @Override
        public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
            Device device = findOrCreateDevice(myo);
            device.setStatus(Device.Status.CONNECTED);
            LOGGER.info("{} connected", device);
        }

        @Override
        public void onRssi(Myo myo, long timestamp, int rssi) {
            Device device = findOrCreateDevice(myo);
            LOGGER.info("{} got RSSI: {}", device, rssi);
        }

        @Override
        public void onBatteryLevelReceived(Myo myo, long timestamp, int level) {
            Device device = findOrCreateDevice(myo);
            LOGGER.info("{} got batery: {}", device, level);
        }

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection, float rotation, WarmupState warmupState) {
            Device device = findOrCreateDevice(myo);
            LOGGER.info("{} arm sync (timestamp: {}, arm: {}, x-direction: {}, rotation: {}, warm-up-state: {})",
                    device, timestamp, arm, xDirection, rotation, warmupState);

            device.setArm(arm);
            device.setStatus(Device.Status.IN_SYNC);
            device.setRotation(rotation);
            device.setXDirection(xDirection);
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            Device device = findOrCreateDevice(myo);
            device.setStatus(Device.Status.OUT_OF_SYNC);
            LOGGER.info("{} arm out of sync (timestamp: {})", device);
        }

        @Override
        public void onWarmupCompleted(Myo myo, long timestamp, WarmupResult warmupResult) {
            Device device = findOrCreateDevice(myo);

            LOGGER.info("{} warm up  complete (timestamp: {}, warm-up-result: {})",
                    device, timestamp, warmupResult);
        }

        @Override
        public void onLock(Myo myo, long timestamp) {
            Device device = findOrCreateDevice(myo);
            device.setLocked(true);
            LOGGER.info("{} locked", device);
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) {
            Device device = findOrCreateDevice(myo);
            device.setLocked(false);
            LOGGER.info("{} unlocked", device);
        }

    };

    private final Hub hub;

    @Autowired
    public ConnectorService(Hub hub) {
        this.hub = hub;
    }

    public void ping(Device device) {
        device.getMyo().vibrate(VibrationType.VIBRATION_SHORT);
    }

    public void refresh() {
        if (true) return;
        Myo myo;
        do {
            myo = hub.waitForMyo(2000);
            if (myo != null && !findDevice(myo).isPresent()) {
                findOrCreateDevice(myo);
            }
        } while (myo != null);
    }

    public ObservableList<Device> getDevices() {
        return devices;
    }

    private Optional<Device> findDevice(Myo myo) {
        if (myo != null) {
            for (Device device : devices) {
                if (myo.equals(device.getMyo())) {
                    return Optional.of(device);
                }
            }
        }
        return Optional.empty();
    }

    private Device findOrCreateDevice(Myo myo) {
        if (myo == null) {
            return null;
        }
        return findDevice(myo).orElseGet(() -> {
            Device device = bind(myo);
            devices.add(device);

            LOGGER.debug("added {}", myo);

            return device;
        });
    }

    @Override
    protected void startUp() throws Exception {
        hub.addListener(listener);
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            hub.run(500);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        hub.removeListener(listener);
    }

    @PostConstruct
    private void start() {
        startAsync();
    }

    @PreDestroy
    private void stop() {
        stopAsync();
    }

    private Device bind(Myo myo) {
        Device device = new Device(myo, hub);
        device.setName(Integer.toString(devices.size()));
        return device;
    }

    public Hub getHub() {
        return hub;
    }

}


