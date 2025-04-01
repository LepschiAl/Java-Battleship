package common;

import java.io.Serializable;

public class Message implements Serializable {
    private int x;
    private int y;
    private Boolean isHit;

    public Message(int x, int y, Boolean isHit) {
        this.x = x;
        this.y = y;
        this.isHit = isHit;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Boolean isHit() {
        return isHit;
    }

    public void setHit(Boolean hit) {
        isHit = hit;
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
