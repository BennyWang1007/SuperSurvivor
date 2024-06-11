package entity;

import main.Game;

public abstract class Entity {
    protected final Game game;
    public float x; // center x
    public float y; // center y
    public int width;
    public int height;
    private final Hitbox hitbox;

    public Entity(Game game, float x, float y, int width, int height) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Hitbox();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Hitbox getHitBox() {
        hitbox.startX = (int) (x - width/2);
        hitbox.startY = (int) (y - height/2);
        hitbox.endX = (int) (x + width/2);
        hitbox.endY = (int) (y + height/2);
        return hitbox;
    }
}
