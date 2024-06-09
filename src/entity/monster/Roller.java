package entity.monster;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import entity.Player;
import main.Game;
import utils.ImageTools;

public class Roller extends Monster {
    private int direction = 0; // 0: right, 1: left
    private BufferedImage[][][] animationImage; // direction | [idle/run] | index

    private final int animFramesPerImage = Game.FPS / 8;
    private int animFrameCounter = 0;
    private int animImageIndex = 0;

    private float rollDistance = 300;
    private float rolledDistance = 0;
    private float chargedDistance = 0;
    private boolean rolling = false;
    private boolean charging = false;
    private float rollDegree = 0;
    private float rollDx = 0;
    private float rollDy = 0;

    public Roller(Game game, String name, int x, int y, int hp, int attack, int speed, int exp, Player player) {
        super(game, name, x, y, hp, attack, speed, exp, player);
        width = 75;
        height = 75;
        loadAnimationImage();
    }

    private void loadAnimationImage() {
        animationImage = new BufferedImage[2][2][4];
        for (int i = 0; i < 4; i++) {
            animationImage[0][0][i] = ImageTools.scaleImage(ImageTools.readImage("/monsters/roller/roller_idle_" + i + ".png"), width, height);
            animationImage[1][0][i] = ImageTools.mirrorImage(animationImage[0][0][i]);
            animationImage[0][1][i] = ImageTools.scaleImage(ImageTools.readImage("/monsters/roller/roller_run_" + i + ".png"), width, height);
            animationImage[1][1][i] = ImageTools.mirrorImage(animationImage[0][1][i]);
        }
    }

    @Override
    public void update() {
        if (rolling) {
            rolledDistance += speedPerFrame;
            if (rolledDistance >= rollDistance) {
                rolling = false;
                rolledDistance = 0;
                chargedDistance = 0;
            }
            x += rollDx;
            y += rollDy;
        } else if (charging) {
            chargedDistance += speedPerFrame * 2;
            if (chargedDistance >= rollDistance) {
                chargedDistance = rollDistance;
                charging = false;
                rolling = true;
            }
        } else {
            rollDegree = (float) Math.toDegrees(Math.atan2(player.y - this.y, player.x - this.x));
            rollDx = speedPerFrame * (float) Math.cos(Math.toRadians(rollDegree));
            rollDy = speedPerFrame * (float) Math.sin(Math.toRadians(rollDegree));
            direction = (rollDx > 0 ? 0 : 1);
            charging = true;
        }


    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        Graphics2D g2d = (Graphics2D) g;
    
        if (charging || rolling) {
            // Calculate the angle to the player
            double angle = Math.toRadians(rollDegree - 90); // -90 to adjust direction

            AffineTransform originalTransform = g2d.getTransform();
            g2d.translate(screenX + width / 2, screenY + height / 2);
            g2d.rotate(angle);
    
            // Draw the trail
            if (charging) {
                g2d.setColor(new Color(139, 0, 0, 100)); // Deep red
                g2d.fillRect(-width / 2, -height / 2, width, (int) chargedDistance);
                g2d.setColor(new Color(255, 102, 102, 100)); // Light red
                g2d.fillRect(-width / 2, (int) (chargedDistance - height / 2), width, (int) (rollDistance - chargedDistance));
            } else {
                g2d.setColor(new Color(139, 0, 0, 100)); // Deep red
                g2d.fillRect(-width / 2, -height / 2, width, (int)(chargedDistance - rolledDistance));
            }
    
            g2d.setTransform(originalTransform);
        }
        
        drawBody(g, screenX, screenY);

        // draw health bar, red and green, above the monster
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = screenX;
        int healthBarY = screenY - healthBarHeight - 15;
        g.setColor(Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);

        drawDamageReceived(g);
        
    }

    private void drawBody(Graphics g, int cx, int cy) {
        int idleRun = (rolling ? 1 : 0);
        g.drawImage(animationImage[direction][idleRun][animImageIndex], cx, cy, width, height, null);
        animFrameCounter++;
        if (animFrameCounter >= animFramesPerImage) {
            animFrameCounter -= animFramesPerImage;
            animImageIndex = (animImageIndex + 1) % 4;
        }
    }
    
}
