package com.tradedoubler.rafty;

import io.atomix.copycat.client.Command;

import java.io.Serializable;

import static com.tradedoubler.rafty.TrackingEvent.Type.*;

/**
 * This command achieves eventual consistency for all replicas.
 * @author qinwa
 */
public class SequentialCommand implements Command<TrackingEvent>, Serializable{
    private final TrackingEvent trackingEvent;

    public SequentialCommand(TrackingEvent trackingEvent) {
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
