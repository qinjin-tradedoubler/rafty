package com.tradedoubler.rafty;

import io.atomix.copycat.client.Command;

import java.io.Serializable;

import static com.tradedoubler.rafty.TrackingEvent.Type.*;

/**
 * @author qinwa
 */
public class TrackingEventCommand implements Command, Serializable{
    private final TrackingEvent trackingEvent;

    public TrackingEventCommand(TrackingEvent trackingEvent) {
        this.trackingEvent = trackingEvent;
    }

    public TrackingEvent getTrackingEvent() {
        return trackingEvent;
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
}
