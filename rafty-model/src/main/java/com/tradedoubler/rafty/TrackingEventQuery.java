package com.tradedoubler.rafty;

import io.atomix.copycat.client.Query;

import java.io.Serializable;

/**
 * @author qinwa
 */
public class TrackingEventQuery implements Query<TrackingEvent>, Serializable {
    private final String trackingEventId;

    public TrackingEventQuery(String trackingEventId){
        this.trackingEventId = trackingEventId;
    }

    public String key(){
        return trackingEventId;
    }
}
