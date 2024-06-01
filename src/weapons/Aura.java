package weapons;

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import entity.Player;
import entity.monster.Monster;
import main.Game;

public class Aura extends Weapon {

    private float radius;
    private int animationIndex = 0;
    Set<Monster> monsters;
    // private float degreeOfTransparency = 0.0f; // cos(degree) = transparency

    public Aura(Game game, int width, int height, int attack, float radius, Player player) {
        super(game, width, height, attack, player);
        readImage("res/Aura.png");
        this.radius = radius;
        monsters = game.getMonsters();
        loadAnimation();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        setSize((int) radius * 2, (int) radius * 2);
        readImage("res/Aura.png");
        loadAnimation();
    }

    public void increaseRadius(float delta) {
        setRadius(radius + delta);
    }

    @Override
    public void update() {
        decreaseCooldowns();
        animationIndex = (animationIndex + 1) % images.length;
        for (Monster monster : monsters) {
            float distance = (float) Math.hypot(monster.x - player.x, monster.y - player.y);
            if (distance <= radius) {
                attackOn(monster);
            }
        }
    }

    @Override
    public void attackOn(Monster monster) {
        if (attackCooldowns.containsKey(monster.id)) {
            return;
        }
        monster.damage(attack);
        attackCooldowns.put(monster.id, (int)(0.5f * Game.FPS)); // 0.5s cooldown
    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(player.x);
        int screenY = game.translateToScreenY(player.y);
        // draw aura with animation
        g.drawImage(images[animationIndex], screenX - width / 2, screenY - height / 2, null);
    }

    @Override
    public void loadAnimation() {
        images = new BufferedImage[Game.FPS * 2];
        float degreePerFrame = 360.0f / images.length;
        for (int i = 0; i < images.length; i++) {
            // use (cos(degree) + 1) / 2 to calculate transparency
            float degree = degreePerFrame * i;
            float transparency = (float) (Math.cos(Math.toRadians(degree)) + 1) / 2;
            images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) images[i].getGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            g.drawImage(image, 0, 0, null);
            g.dispose();

        }
    }
    
}
