package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.staff_devices.StaffDevice;

public class AlertPublisher {

    private List<StaffDevice> subscribers;
    private static AlertPublisher instance;

    /**
     * I use a singleton design pattern to ensure that only one instance of the AlertPublisher is created:
     * The Constructor is private and the getInstance method is static.
     */
    private AlertPublisher() {
        this.subscribers = new ArrayList<StaffDevice>();
    }
    public static AlertPublisher getInstance() {
        if(instance == null) {
            instance = new AlertPublisher();
        }
        return instance;
    }

    public void subscribe(StaffDevice device) {
        subscribers.add(device);
    }

    public void unsubscribe(StaffDevice device) {
        subscribers.remove(device);
    }

    public void publishAlert(Alert alert) {
        for(StaffDevice device : subscribers) {
            device.displayAlert(alert);
        }
    }
}
