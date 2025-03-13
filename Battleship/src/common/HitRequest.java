package common;

import java.io.Serializable;

public class HitRequest implements Serializable {
    int x;
    int y;

    public HitRequest(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
