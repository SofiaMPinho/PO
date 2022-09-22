package ggc;

import java.io.IOException;
import java.io.Serializable;

public abstract class DeliveryMethod implements Serializable {
    /** function to override in other classes */
    public void sendNotification(NotificationX n){

    }
}