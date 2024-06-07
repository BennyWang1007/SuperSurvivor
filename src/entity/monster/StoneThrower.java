package entity.monster;

import java.awt.Graphics;

import entity.Player;
import main.Game;

public class StoneThrower extends Monster {

    private float shootRange = 500;
    public StoneThrower(Game game, String name, int x, int y, int hp, int attack, int speed, int exp, Player player) {
        super(game, name, x, y, hp, attack, speed, exp, player);
    }

    public void update() {
        // move towards player
        float dx = player.x - this.x;
        float dy = player.y - this.y;
        float distance = (float)Math.hypot(dx, dy);
        if (distance < shootRange) {
            shoot();
            return ;
        };
        
        dx *= speedPerFrame / distance;
        dy *= speedPerFrame / distance;
        
        x += dx;
        y += dy;
    }

    private int shootCooldown = 0;

    private void shoot() {
        if (shootCooldown > 0) {
            shootCooldown--;
            return;
        }
        shootCooldown = Game.FPS;
        float degree = (float)Math.toDegrees(Math.atan2(player.y - y, player.x - x));
        game.addProjectile(new Stone(game, x, y, 20, 20, 10, 5, degree, player));
    }

    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLUE);
        g.setFont(g.getFont().deriveFont(12f));
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

        drawDamageReceived(g);

        // // draw hitbox
        // g.setColor(java.awt.Color.RED);
        // g.drawRect(cx, cy, width, height);
    }
}
