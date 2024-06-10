package entity;

import java.awt.Color;
import java.awt.Graphics;

import main.*;

public class ExpOrb extends DropItem {
    
    private int exp;
    private Sound collectSound;

    public ExpOrb(Game game, float x, float y, int exp, Player player) {
        super(game, x, y, 8, 8, player);
        this.exp = exp;
        collectSound = new Sound("exp_collect.wav");
    }

    public void beCollected() {
        player.addExp(exp);
        if (game.settings.isSoundOn()) collectSound.play();
        player.addScore(exp / 2);
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