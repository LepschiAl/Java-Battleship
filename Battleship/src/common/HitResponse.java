package common;

import java.io.Serializable;

public class HitResponse implements Serializable {
    boolean isHit;

    public HitResponse(boolean isHit) {
        this.isHit = isHit;
    }
}
