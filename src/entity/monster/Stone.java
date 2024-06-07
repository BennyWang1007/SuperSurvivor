package entity.monster;

import java.awt.Graphics;

import weapons.Projectile;
import entity.Player;
import main.Game;

public class Stone extends Projectile {

    private Player player;
    private int cooldown;

    public Stone(Game game, float x, float y, int width, int height, int attack, float speed, float degree, Player player) {
        super(game, x, y, width, height, attack, speed, degree);
        this.player = player;
    }

    @Override
    public void update() {
        move();
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (!game.isValidPosition(x, y) || !game.isInScreen(x, y)) {
            toDelete = true;
        }
        if (player.getHitBox().isCollideWith(getHitBox())) {
            attackOn(player);
            cooldown = Game.FPS;
            // toDelete = true;
        }
    }

    @Override
    public void attackOn(Monster monster) {
        throw new UnsupportedOperationException("Should not attack on monster!");
    }

    @Override
    public void attackOn(Player player) {
        player.collideWith(this);
    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(cx, cy, width, height);
        g.fillRect(cx, cy, width, height);
    }
    
}
