package com.tradedoubler.rafty;

/**
 * @author qinwa
 */
public class Trackback extends TrackingEvent {
    public final int guidHash;
    private int clickId = -1;

    public Trackback(String id, int adId, int siteId, int guidHash, long timeOfEvent){
        super(id, adId, siteId, timeOfEvent);
        this.guidHash = guidHash;
    }

    public int getClickId() {
        return clickId;
    }

    public void setClickId(int clickId) {
        this.clickId = clickId;
    }

    @Override
    public Type getType() {
        return Type.Trackback;
    }
}
