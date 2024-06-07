package weapons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


import entity.Player;
import entity.monster.Monster;
import main.Game;
import utils.ImageTools;

public class Arrow extends Weapon {

    private float degree;
    private float speed; // in pixels per update
    public boolean toDelete = false;

    public Arrow(Game game, float x, float y, int width, int height, int attack, float degree, float speed, Player player) {
        super(game, width, height, attack, player);
        readImage("/Arrow.png");
        this.degree = degree;
        this.speed = speed;
        this.x = x;
        this.y = y;
        
        // rotate image
        image = ImageTools.rotateImage(originalImage, degree);
    }

    @Override
    public void update() {
        x += Math.cos(Math.toRadians(degree)) * speed;
        y += Math.sin(Math.toRadians(degree)) * speed;
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

    @Override
    public void loadAnimation() {
        // TODO
    }

    @Override
    public void levelUp() {

    }

}
