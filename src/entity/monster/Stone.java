package entity.monster;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utils.ImageTools;
import weapons.Projectile;
import entity.Player;
import main.Game;

public class Stone extends Projectile {
    private static int fixedWidth = 20;
    private static int fixedHeight = 20;
    private static BufferedImage[] animationImage = loadAnimationImage();
    private final int animFramesPerImage = Game.FPS / 8;
    private int animFrameCounter = 0;
    private int animImageIndex = 0;
    private int cooldown;

    public Stone(Game game, float x, float y, int attack, float speed, float degree) {
        super(game, x, y, fixedWidth, fixedHeight, attack, speed, degree);
    }

    private static BufferedImage[] loadAnimationImage() {
        BufferedImage[] images = new BufferedImage[4];
        for (int i = 0; i < 4; i++) {
            images[i] = ImageTools.scaleImage(ImageTools.readImage("/monsters/necromancer/ball" + i + ".png"), fixedWidth, fixedHeight);
        }
        return images;
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
    }

    @Override
    public void attackOn(Monster monster) {
        throw new UnsupportedOperationException("Should not attack on monster!");
    }

    @Override
    public void attackOn(Player player) {
        player.collideWith(this);
        cooldown = Game.FPS;
        toDelete = true;
    }

    @Override
    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLACK);
        drawBody(g, cx, cy);

        if (Game.DEBUG) {
            getHitBox().draw(g);
        }
    }

    private void drawBody(Graphics g, int cx, int cy) {
        g.drawImage(animationImage[animImageIndex], cx, cy, width, height, null);
        animFrameCounter++;
        if (animFrameCounter >= animFramesPerImage) {
            animFrameCounter -= animFramesPerImage;
            animImageIndex = (animImageIndex + 1) % 4;
        }
    }
}
