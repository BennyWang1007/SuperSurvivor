package weapons;

import java.awt.*;

import entity.Player;
import entity.monster.Monster;
import main.Game;
import utils.ImageTools;

public class Arrow extends Projectile {

    public Arrow(Game game, float x, float y, int width, int height, int attack, float speed, float degree, Player player) {
        super(game, x, y, width, height, attack, speed, degree);
        originalImage = ImageTools.scaleImage(ImageTools.readImage("/weapons/Arrow.png"), width, height);
        image = ImageTools.rotateImage(originalImage, degree);
    }

    @Override
    public void update() {
        move();
        for (Monster monster : game.getMonsters()) {
            if (monster.getHitBox().isCollideWith(getHitBox())) {
                attackOn(monster);
                break;
            }
        }
        if (!game.isValidPosition(x, y) || !game.isInScreen(x, y)) {
            toDelete = true;
        }
    }

    @Override
    public void attackOn(Monster monster) {
        monster.damage(attack);
        toDelete = true;
    }

    @Override
    public void attackOn(Player player) {
        throw new UnsupportedOperationException("Should not attack on player!");
    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        
        g.drawImage(image, cx, cy, null);

        // draw hitbox
//        g.setColor(Color.RED);
//        ((Graphics2D) g).drawRect(cx, cy, width, height);

    }
}
