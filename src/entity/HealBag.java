package entity;

import java.awt.Color;
import java.awt.Graphics;

import main.Game;

public class HealBag extends DropItem {

    private int heal;

    public HealBag(Game game, float x, float y, int heal, Player player) {
        super(game, x, y, 8, 8, player);
        this.heal = heal;
    }

    public void beCollected() {
        player.heal(heal);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        // draw a rhombus
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        int[] xPoints = {screenX, screenX + width/2, screenX, screenX - width/2};
        int[] yPoints = {screenY - height/2, screenY, screenY + height/2, screenY};
        g.fillPolygon(xPoints, yPoints, 4);
    }
    
}
