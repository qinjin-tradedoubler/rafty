/**
 * @author qinwa
 */
public class Trackback {
    public final int id;
    public final int adId;
    public final int siteId;
    public final int guidHash;
    public final long timeOfEvent;

    private int clickId = -1;

    public Trackback(int id, int adId, int siteId, int guidHash, long timeOfEvent){
        this.id = id;
        this.adId = adId;
        this.siteId = siteId;
        this.guidHash = guidHash;
        this.timeOfEvent = timeOfEvent;
    }

    public int getClickId() {
        return clickId;
    }

    public void setClickId(int clickId) {
        this.clickId = clickId;
    }
}
