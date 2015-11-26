package com.tradedoubler.rafty;

/**
 * @author qinwa
 */
public abstract class TrackingEvent {
    public enum Type{
        Click, Trackback, Impression
    }

    public final int id;
    public final long timeOfEvent;
    public final int adId;
    public final int siteId;

    public TrackingEvent(int id, int adId, int siteId, long timeOfEvent) {
        this.id = id;
        this.adId = adId;
        this.siteId = siteId;
        this.timeOfEvent = timeOfEvent;
    }

    public abstract Type getType();
}
