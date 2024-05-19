package entity;

public class Hitbox {
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;

    public Hitbox(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
