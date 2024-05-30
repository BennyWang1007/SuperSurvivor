package entity;

public class Hitbox {
    public int startX;
    public int startY;
    public int endX;
    public int endY;

    public Hitbox() {
        this(0, 0, 0, 0);
    }

    public Hitbox(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public boolean isCollideWith(Hitbox other) {
        if (startX < other.startX && endX < other.startX) return false;
        if (other.startX < startX && other.endX < startX) return false;
        if (startY < other.startY && endY < other.startY) return false;
        if (other.startY < startY && other.endY < startY) return false;
        return true;
    }
}
