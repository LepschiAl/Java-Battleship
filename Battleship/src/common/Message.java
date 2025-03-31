package common;

import java.io.Serializable;

public class Message implements Serializable {
    public int x;
    public int y;
    public boolean isHit;

    public Message(int x, int y, boolean isHit) {
        this.x = x;
        this.y = y;
        this.isHit = isHit;
    }

    @Override
    public String toString() {
        return "Message{" +
                "x=" + x +
                ", y=" + y +
                ", isHit=" + isHit +
                '}';
    }
}
