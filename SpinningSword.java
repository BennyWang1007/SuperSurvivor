import java.awt.geom.AffineTransform;
import java.awt.*;

public class SpinningSword extends Weapon{
    
    private float degreePerSecond;
    private float distance;
    private float degree;

    public SpinningSword(int width, int height, int attack, float degreePerSecond, float distance, Player owner){
        super(width, height, attack, owner);
        this.degreePerSecond = degreePerSecond;
        this.distance = distance;
        this.degree = 0;
        readImage("assets/sword_900.png");
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
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);
        ((Graphics2D) g).drawImage(image, at, null);
        // ((Graphics2D) g).drawRect(loc_x, loc_y, width+5, height+5);
    }

    public void loadAnimation() {};
}