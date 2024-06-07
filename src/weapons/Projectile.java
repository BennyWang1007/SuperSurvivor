package weapons;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import entity.Entity;
import entity.monster.Monster;
import entity.Player;
import main.Game;
import utils.ImageTools;

public abstract class Projectile extends Entity {

    public int attack;
    protected float degree;
    protected float speed; // in pixels per update
    public boolean toDelete = false;

    protected BufferedImage originalImage;
    protected BufferedImage image;

    public Projectile(Game game, float x, float y, int width, int height, int attack, float speed, float degree) {
        super(game, x, y, width, height);
        this.attack = attack;
        this.degree = degree;
        this.speed = speed;
    }

    protected void readImage(String imageName) {
        originalImage = ImageTools.scaleImage(ImageTools.readImage(imageName), width, height);
        image = originalImage;
    }

    protected void move() {
        x += Math.cos(Math.toRadians(degree)) * speed;
        y += Math.sin(Math.toRadians(degree)) * speed;
    }

    public abstract void update();
    public abstract void attackOn(Monster monster);
    public abstract void attackOn(Player player);
    public abstract void draw(Graphics g);

}
