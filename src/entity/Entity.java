package entity;

public abstract class Entity {
    public float x; // center x
    public float y; // center y
    public int width;
    public int height;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Hitbox getHitBox() {
        return new Hitbox((int)(x-width/2), (int)(y-height/2), (int)(x+width/2), (int)(y+height/2));
    }
}
