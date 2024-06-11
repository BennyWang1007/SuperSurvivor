package entity.monster;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import entity.Player;
import entity.DropItem;
import main.Game;
import utils.ImageTools;

public class KnifeGoblin extends Monster {
    private int direction = 0; // 0: right, 1: left
    private static int fixedWidth = 50;
    private static int fixedHeight = 50;
    private static BufferedImage[][] animationImage = loadAnimationImage(); // direction | index

    private final int animFramesPerImage = Game.FPS / 8;
    private int animFrameCounter = 0;
    private int animImageIndex = 0;

    private static final int defaltHp = 90;
    private static final int defaultAttack = 20;
    private static final int defaultSpeed = 60;
    private static final int defaultExp = 20;
    
    public KnifeGoblin(Game game, String name, int x, int y, double strength, Player player) {
        super(game, name, x, y, defaltHp, defaultAttack, defaultSpeed, defaultExp, strength, player);
        dropItems = new DropItem[] {
            (DropItem) new entity.ExpOrb(game, x, y, exp, player),
            (DropItem) new entity.HealBag(game, x, y, (int)(10 * strength), player)
        };
        dropRates = new float[] {1.0f, 0.1f};
    }

    private static BufferedImage[][] loadAnimationImage() {
        BufferedImage[][] images = new BufferedImage[2][4];
        for (int i = 0; i < 4; i++) {
            images[0][i] = ImageTools.scaleImage(ImageTools.readImage("/monsters/goblin/goblin" + i + ".png"), fixedWidth, fixedHeight);
            images[1][i] = ImageTools.mirrorImage(images[0][i]);
        }
        return images;
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

        if (Game.DEBUG) {
            getHitBox().draw(g);
        }
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
