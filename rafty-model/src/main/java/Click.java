/**
 * @author qinwa
 */
public class Click {
    public final int id;
    public final int adId;
    public final int siteId;
    public final int guidHash;
    public final long timeOfEvent;

    public Click(int id, int adId, int siteId,  int guidHash, long timeOfEvent){
        this.id = id;
        this.adId = adId;
        this.siteId = siteId;
        this.guidHash = guidHash;
        this.timeOfEvent = timeOfEvent;
    }
}
