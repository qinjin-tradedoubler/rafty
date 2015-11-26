package com.tradedoubler.rafty;

import java.io.Serializable;

/**
 * @author qinwa
 */
public abstract class TrackingEvent implements Serializable{
    public enum Type{
        Click, Trackback, Impression
    }

    public final String id;
    public final long timeOfEvent;
    public final int adId;
    public final int siteId;

    public TrackingEvent(String id, int adId, int siteId, long timeOfEvent) {
        this.id = id;
        this.adId = adId;
        this.siteId = siteId;
        this.timeOfEvent = timeOfEvent;
    }

    public abstract Type getType();

    @Override
    public String toString(){
//        String str = getType()+" - id="+id+" - adId="+adId+" - siteId="+siteId+" - timeOfEvent="+timeOfEvent;
//        return str;
        return id;
    }
}
