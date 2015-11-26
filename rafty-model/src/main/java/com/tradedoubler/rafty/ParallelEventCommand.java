package com.tradedoubler.rafty;

import io.atomix.copycat.client.Command;

import java.io.Serializable;

/**
 * This command achieves Eventual consistency for all replicas.
 * @author qinwa
 */
public class ParallelEventCommand implements Command<TrackingEvent>, Serializable {
    private final TrackingEvent trackingEvent;

    public ParallelEventCommand(TrackingEvent trackingEvent) {
        this.trackingEvent = trackingEvent;
    }

    @Override
    public ConsistencyLevel consistency(){
        return ConsistencyLevel.NONE;
    }

    public String key(){
        return trackingEvent.id;
    }

    public TrackingEvent value() {
        return trackingEvent;
    }
}
