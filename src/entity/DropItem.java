package entity;

import java.awt.Graphics;
import main.Game;

public abstract class DropItem extends Entity {

    protected static final float defaultSpeed = 150.0f / Game.FPS;
    protected static final float speedMultiplier = (1.0f + 2.0f / Game.FPS);
    protected float speedUpThreshold = 150.0f;
    protected float speed = defaultSpeed;

    public boolean isCollected = false;

    protected final Player player;
    
    public DropItem(Game game, float x, float y, int width, int height, Player player) {
        super(game, x, y, width, height);
        this.player = player;
        speedUpThreshold = player.collectRange;
        speed = defaultSpeed;
    }

    // public abstract void update();
    public void update() {
        float dx = player.x - x;
        float dy = player.y - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (player.getHitBox().isCollideWith(getHitBox())) { // player get drop item
            beCollected();
            isCollected = true;
        } else if(distance > speedUpThreshold) { // escape the range
            speed = defaultSpeed;
        } else if (speed != 0) { // in the range, move towards player and speed up
            x += dx / distance * speed;
            y += dy / distance * speed;
            speed *= speedMultiplier;
        }
    }

    public abstract void beCollected();

    public abstract void draw(Graphics g);

}
