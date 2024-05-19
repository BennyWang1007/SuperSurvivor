package weapons;

import java.awt.geom.AffineTransform;
import java.awt.*;

import main.*;

public class SpinningSword extends Weapon{
    
    private float degreePerSecond;
    private float distance;
    private float degree;

    public SpinningSword(int width, int height, int attack, float degreePerSecond, float distance, Player owner){
        super(width, height, attack, owner);
        this.degreePerSecond = degreePerSecond;
        this.distance = distance;
        this.degree = 0;
        readImage("res/sword_900.png");
    }

    public void update() {
        playerX = owner.x;
        playerY = owner.y;
        degree += degreePerSecond / FPS;

        if (degree >= 360) { degree -= 360; }

        offsetX = (int) (Math.cos(Math.toRadians(degree)) * distance);
        offsetY = (int) (Math.sin(Math.toRadians(degree)) * distance);

        x = playerX + offsetX;
        y = playerY + offsetY;
        // System.out.println("Weapon at " + x + ", " + y + " with degree " + degree);
        // System.out.println("FPS: " + FPS + "(" + gamePanel.getFPS() + ")");
        collisionCheck();
    }

    public void draw(Graphics g) {
        // System.out.println("Painting weapon");
        int cx = x - playerX + owner.getGamePanel().getWidth() / 2;
        int cy = y - playerY + owner.getGamePanel().getHeight() / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(cx, cy);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);
        ((Graphics2D) g).drawImage(image, at, null);
        ((Graphics2D) g).drawRect(cx, cy, image.getWidth()+5, image.getHeight()+5);
    }

    public void loadAnimation() {};
}