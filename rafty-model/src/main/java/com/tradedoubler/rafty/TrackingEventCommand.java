package com.tradedoubler.rafty;

import io.atomix.copycat.client.Command;

import java.io.Serializable;

import static com.tradedoubler.rafty.TrackingEvent.Type.*;

/**
 * @author qinwa
 */
public class TrackingEventCommand implements Command<TrackingEvent>, Serializable{
    private final TrackingEvent trackingEvent;

    public TrackingEventCommand(TrackingEvent trackingEvent) {
        this.trackingEvent = trackingEvent;
    }

    @Override
    public ConsistencyLevel consistency(){
        switch (trackingEvent.getType()){
            case Click:
            case Trackback:
                return ConsistencyLevel.SEQUENTIAL;
            case Impression:
                return ConsistencyLevel.NONE;
            default:
                return ConsistencyLevel.SEQUENTIAL;
        }
    }

    public String key(){
        return trackingEvent.id;
    }

    public TrackingEvent value() {
        return trackingEvent;
    }
}
