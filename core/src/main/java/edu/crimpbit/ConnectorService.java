package edu.crimpbit;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Service wrapper for connector/hub.
 */
public class ConnectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorService.class);

    private static final String HUB_ID = "edu.crimpbit.analysis";

    private final List<Myo> myos = Collections.synchronizedList(new LinkedList<>());
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
            }

            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                LOGGER.info("{} {} disconnected", myo, identify(myo));
            }

            @Override
            public void onConnect(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
                LOGGER.info("{} {} connected", myo, identify(myo));
            }

            private String identify(Myo myo) {
                return Integer.toString(myos.indexOf(myo));
            }

        });
    }

    public Collection<Myo> getMyos() {
        Myo myo = hub.waitForMyo(10000);
        if (myo != null) {
            myos.add(myo);
        }
        return myos;
    }

    public Hub getHub() {
        return hub;
    }

}


