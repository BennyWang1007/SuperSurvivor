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
}
