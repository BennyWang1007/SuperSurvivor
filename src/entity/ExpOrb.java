package entity;

import java.awt.Color;
import java.awt.Graphics;

import main.*;

public class ExpOrb extends Entity {
    
    private static final float defaultSpeed = 150.0f / Game.FPS;
    private static final float speedMultiplier = (1.0f + 2.0f / Game.FPS);
    private static final float speedUpThreshold = 150.0f;

    private int exp;
    private Player player;
    private float speed = 0;
    private float distance;
    public boolean isCollected = false;

    public ExpOrb(Game game, float x, float y, int exp, Player player) {
        super(game, x, y, 8, 8);
        this.exp = exp;
        this.player = player;
        this.speed = defaultSpeed;
    }

    public void update() {
        float dx = player.x - x;
        float dy = player.y - y;
        distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (player.getHitBox().isCollideWith(getHitBox())) { // player get exp
            player.addExp(exp);
            isCollected = true;
        } else if(distance > speedUpThreshold) { // escape the range
            speed = defaultSpeed;
        } else if (speed != 0) { // in the range, move towards player and speed up
            x += dx / distance * speed;
            y += dy / distance * speed;
            speed *= speedMultiplier;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        // draw a rhombus
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        int[] xPoints = {screenX, screenX + width/2, screenX, screenX - width/2};
        int[] yPoints = {screenY - height/2, screenY, screenY + height/2, screenY};
        g.fillPolygon(xPoints, yPoints, 4);
    }
     
}