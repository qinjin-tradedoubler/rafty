package com.tradedoubler.rafty;

import io.atomix.copycat.client.Query;

/**
 * @author qinwa
 */
public class TrackingEventQuery implements Query<TrackingEvent> {
    private final String trackingEventId;

    public TrackingEventQuery(String trackingEventId){
        this.trackingEventId = trackingEventId;
    }

    public String key(){
        return trackingEventId;
    }
}
