package common;

import java.io.Serializable;

public class Message implements Serializable {
    int x;
    int y;
    boolean isHit;

    public Message(int x, int y, boolean isHit) {
        this.x = x;
        this.y = y;
        this.isHit = isHit;
    }
}
