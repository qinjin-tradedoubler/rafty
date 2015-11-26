package com.tradedoubler.rafty;

/**
 * @author qinwa
 */
public class Click extends TrackingEvent {
    public final int guidHash;

    public Click(int id, int adId, int siteId,  int guidHash, long timeOfEvent){
        super(id, adId, siteId, timeOfEvent);
        this.guidHash = guidHash;
    }

    @Override
    public Type getType() {
        return Type.Click;
    }
}
