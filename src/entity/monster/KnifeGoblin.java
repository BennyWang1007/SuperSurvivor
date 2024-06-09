package entity.monster;

import java.awt.Graphics;

import entity.Player;
import entity.DropItem;
import main.Game;

public class KnifeGoblin extends Monster {
    
    public KnifeGoblin(Game game, String name, int x, int y, int hp, int attack, int speed, int exp, Player player) {
        super(game, name, x, y, hp, attack, speed, exp, player);
        dropItems = new DropItem[] {
            (DropItem) new entity.ExpOrb(game, x, y, exp, player)
        };
        dropRates = new float[] {1.0f};
    }

    
    public void update() {
        // move towards player
        float dx = player.x - this.x;
        float dy = player.y - this.y;
        float distance = (float)Math.hypot(dx, dy);
        if (distance < 1) return;

        dx *= speedPerFrame / distance;
        dy *= speedPerFrame / distance;
        x += dx;
        y += dy;
    }

    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLACK);
        g.setFont(g.getFont().deriveFont(12f));
        g.drawRect(cx, cy, width, height);
        g.fillRect(cx, cy, width, height);
        // use id instead of name
        g.drawString(Integer.toString(id), cx, cy - 5);

        // draw health bar, red and green, above the monster
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = cx;
        int healthBarY = cy - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
        g.setColor(java.awt.Color.BLACK);

        drawDamageReceived(g);

        // // draw hitbox
        // g.setColor(java.awt.Color.RED);
        // g.drawRect(cx, cy, width, height);
    }
}
