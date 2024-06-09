package entity.monster;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import entity.Player;
import main.Game;
import utils.ImageTools;

public class KnifeGoblin extends Monster {
    private int direction = 0; // 0: right, 1: left
    private BufferedImage[][] animationImage; // direction | index

    private final int animFramesPerImage = Game.FPS / 8;
    private int animFrameCounter = 0;
    private int animImageIndex = 0;
    
    public KnifeGoblin(Game game, String name, int x, int y, int hp, int attack, int speed, int exp, Player player) {
        super(game, name, x, y, hp, attack, speed, exp, player);
        loadAnimationImage();
    }

    private void loadAnimationImage() {
        animationImage = new BufferedImage[2][4];
        for (int i = 0; i < 4; i++) {
            animationImage[0][i] = ImageTools.scaleImage(ImageTools.readImage("/monsters/goblin/goblin" + i + ".png"), width, height);
            animationImage[1][i] = ImageTools.mirrorImage(animationImage[0][i]);
        }
    }
    
    public void update() {
        // move towards player
        float dx = player.x - this.x;
        float dy = player.y - this.y;
        float distance = (float)Math.hypot(dx, dy);
        if (distance < 1) return;

        direction = (dx > 0 ? 0 : 1);

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
        drawBody(g, cx, cy);
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

    private void drawBody(Graphics g, int cx, int cy) {
        g.drawImage(animationImage[direction][animImageIndex], cx, cy, width, height, null);
        animFrameCounter++;
        if (animFrameCounter >= animFramesPerImage) {
            animFrameCounter -= animFramesPerImage;
            animImageIndex = (animImageIndex + 1) % 4;
        }
    }
}
