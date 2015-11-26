package com.tradedoubler.rafty;

/**
 * @author qinwa
 */
public class Impressoin extends TrackingEvent{

    public Impressoin(String id, int adId, int siteId, long timeOfEvent){
        super(id, adId, siteId, timeOfEvent);
    }


    @Override
    public Type getType() {
        return Type.Impression;
    }
}
