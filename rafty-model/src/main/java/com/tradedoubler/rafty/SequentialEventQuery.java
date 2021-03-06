package com.tradedoubler.rafty;

import io.atomix.copycat.client.Query;

import java.io.Serializable;

/**
 * @author qinwa
 */
public class SequentialEventQuery implements Query<TrackingEvent>, Serializable {
    private final String trackingEventId;

    public SequentialEventQuery(String trackingEventId){
        this.trackingEventId = trackingEventId;
    }

    public String key(){
        return trackingEventId;
    }

    public  ConsistencyLevel consistency() {
        return ConsistencyLevel.LINEARIZABLE;
    }
}
