package weapons;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

import entity.*;
import entity.monster.Monster;
import main.Game;

public abstract class Weapon extends Entity{
    protected Player player;
    protected int attack;
    protected HashMap<Integer, Integer> attackCooldowns;
    protected BufferedImage image;
    protected BufferedImage[] images; // for animation
    protected BufferedImage originalImage;
    protected float cooldownTime; // in seconds

    public Weapon(Game game, int width, int height, int attack, Player player) {
        super(game, 0, 0, width, height);
        this.attack = attack;
        this.player = player;
        // loadAnimation();
        attackCooldowns = new HashMap<>();
    }

    protected void readImage(String imageName) {
        Image img = null;
        try {
            img = ImageIO.read(new File(imageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(img, 0, 0, width, height, null);
        image = originalImage;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
    }

    public void decreaseCooldowns() {
        // cooldowns-- and remove cooldowns <= 0
        Iterator<Integer> iterator = attackCooldowns.keySet().iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            attackCooldowns.put(id, attackCooldowns.get(id) - 1);
            if (attackCooldowns.get(id) <= 0) {
                iterator.remove();
            }
        }
    }

    public abstract void update();
    public abstract void attackOn(Monster monster);
    public abstract void draw(Graphics g);
    public abstract void loadAnimation();
}
