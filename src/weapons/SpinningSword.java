package weapons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import entity.Player;
import entity.monster.Monster;
import main.Game;
import main.Sound;
import utils.ImageTools;

public class SpinningSword extends Weapon{
    
    private float degreePerSecond;
    private float distance;
    private float degree;

    private SpinningSword[] childSwords;
    private Sound hitSound;

    public SpinningSword(Game game, int width, int height, float attackMul, float degreePerSecond, float distance, Player owner){
        super(game, width, height, attackMul, owner);
        this.degreePerSecond = degreePerSecond;
        this.distance = distance;
        this.degree = 0;
        readImage("/weapons/Sword.png");
        cooldownTime = 0.5f;
        this.childSwords = new SpinningSword[4];
        hitSound = new Sound("hit_monster.wav");
    }

    public void update() {
        degree += degreePerSecond / Game.FPS;
        if (degree >= 360) { degree -= 360; }

        int offsetX = (int)(Math.cos(Math.toRadians(degree)) * distance);
        int offsetY = (int)(Math.sin(Math.toRadians(degree)) * distance);
        x = player.x + offsetX;
        y = player.y + offsetY;
        decreaseCooldowns();

        for (int i = 0; i < level - 1; i++) {
            childSwords[i].update();
        }
    }

    @Override
    public void setAttack(int attack) {
        this.attack = (int)(attack * attackMul);
        for (int i = 0; i < level - 1; i++) {
            childSwords[i].setAttack(attack);
        }
    }

    public void attackOn(Monster monster) {
        if (attackCooldowns.containsKey(monster.id)) {
            return;
        }
        if (game.settings.isSoundOn()) hitSound.play();
        monster.damage(attack);
        attackCooldowns.put(monster.id, Game.FPS);
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing weapon");
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        AffineTransform at = AffineTransform.getTranslateInstance(cx, cy);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);
        ((Graphics2D) g).drawImage(image, at, null);

        for (int i = 0; i < level - 1; i++) {
            childSwords[i].draw(g);
            // draw string to show which sword is this
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), screenX, screenY);
        }

        // draw hitbox
//        g.setColor(Color.RED);
//        ((Graphics2D) g).drawRect(cx, cy, width, height);
    }

    public void loadAnimation() {}

    @Override
    public void levelUp() {
        level++;
        // set every degree of spinning sword
        degree = 0;
        childSwords[level - 2] = new SpinningSword(game, width, height, attackMul, degreePerSecond, distance, player);
        for (int i = 0; i < level - 1; i++) {
            childSwords[i].degree = 360 / (level - 1) * i;
        }
    }

    @Override
    protected BufferedImage loadIconImage() {
        return ImageTools.rotateImage(ImageTools.readImage("/weapons/Sword.png"), -45);
    }
}