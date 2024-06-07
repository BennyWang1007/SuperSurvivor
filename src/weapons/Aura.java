package weapons;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

import entity.Player;
import entity.monster.Monster;
import main.Game;
import utils.ImageTools;

public class Aura extends Weapon {

    private float radius;
    private int animationIndex = 0;
    Set<Monster> monsters;
    private final float deltaRadius = 20;
    // private float degreeOfTransparency = 0.0f; // cos(degree) = transparency

    public Aura(Game game, int width, int height, float attackMul, float radius, Player player) {
        super(game, width, height, attackMul, player);
        readImage("/Aura.png");
        this.radius = radius;
        monsters = game.getMonsters();
        loadAnimation();
    }

    @Override
    public void update() {
        decreaseCooldowns();
        animationIndex = (animationIndex + 1) % animationImages[0].length;
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
        attackCooldowns.put(monster.id, (int) (0.5f * Game.FPS)); // 0.5s cooldown
    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(player.x);
        int screenY = game.translateToScreenY(player.y);
        // draw aura with animation
        g.drawImage(animationImages[level-1][animationIndex], screenX - width / 2, screenY - height / 2, null);
    }

    @Override
    public void loadAnimation() {
        new Thread(() -> {
            animationImages = new BufferedImage[maxLevel][Game.FPS * 2];
            int tmpWidth = width;
            int tmpHeight = height;
            float tmpRadius = radius;
            BufferedImage tmpImage = image;
            for (int lv = 0; lv < maxLevel; lv++) {
                float degreePerFrame = 360.0f / animationImages[lv].length;
                for (int i = 0; i < animationImages[lv].length; i++) {
                    // use (cos(degree) + 1) / 2 to calculate transparency
                    float degree = degreePerFrame * i;
                    float transparency = (float) (Math.cos(Math.toRadians(degree)) + 1) / 2;
                    animationImages[lv][i] = new BufferedImage(tmpWidth, tmpHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = animationImages[lv][i].createGraphics();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
                    g.drawImage(tmpImage, 0, 0, null);
                    g.dispose();
                }
                tmpRadius += deltaRadius;
                tmpWidth = (int) tmpRadius * 2;
                tmpHeight = (int) tmpRadius * 2;
                tmpImage = ImageTools.scaleImage(image, tmpWidth, tmpHeight);
            }
        }).start();
    }

    @Override
    public void levelUp() {
        if (level == maxLevel) return;
        level++;
        radius += deltaRadius;
        width = (int) radius * 2;
        height = (int) radius * 2;
    }

}
