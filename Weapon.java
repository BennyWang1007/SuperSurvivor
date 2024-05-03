import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import javax.imageio.ImageIO;



public class Weapon {

    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private int playerX;
    private int playerY;

    private int attack;
    private float degree;
    private BufferedImage image;

    private BufferedImage originalImage;
    private float degreePerSecond;
    private int FPS;
    private float distance;
    private int width;
    private int height;

    private String imageName;
    // private int degreeIndex;
    // private BufferedImage[] images; // for animation

    private Monster[] monsters;
    private int monsterCount;

    public Weapon(String imageName, int distance, float degreePerSecond, int width, int height, int attack) {
        // this.originalImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Image img = null;
        try {
            img = ImageIO.read(new File("assets/sword_900.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(img, 0, 0, width, height, null);
        image = originalImage;
        // this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        // this.originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.image = originalImage;
        this.distance = distance;
        this.degree = 0;
        this.degreePerSecond = degreePerSecond;
        this.FPS = 60;
        this.attack = attack;
        // this.images = new BufferedImage[360 / 3];
        playerX = 0;
        playerY = 0;
        x = 0;
        y = 0;
        // loadAnimation();
        monsterCount = 0;
        update();
    }

    public void setMonsters(Monster[] monsters) {
        this.monsters = monsters;
    }

    public void loadAnimation() {
        // originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);

        // this.image = originalImage;
        for (int i = 0; i < 360 / 3; i++) {
            String name = imageName + "_" + i * 3 + ".png";
            try {
                // images[i] = ImageIO.read(new File(name));
                // TODO: resize the image
            } catch (Exception e) {
                System.out.println("Error loading image: " + name);
                e.printStackTrace();
            }
        }
    }

    public void setSize(int width, int height) {
        // originalImage = originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
    }

    public void setMonsterCount(int monsterCount) {
        this.monsterCount = monsterCount;
    }

    public void collisionCheck() {
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].x < x + width && monsters[i].x + monsters[i].width > x && monsters[i].y < y + height && monsters[i].y + monsters[i].height > y) {
                monsters[i].damage(attack);
            }
        }
    }

    public void update() {
        degree += degreePerSecond / FPS;
        if (degree >= 360) {
            degree -= 360;
        }
        offsetX = (int) (Math.cos(Math.toRadians(degree)) * distance);
        offsetY = (int) (Math.sin(Math.toRadians(degree)) * distance);

        x = playerX + offsetX;
        y = playerY + offsetY;
        // System.out.println("Weapon at " + x + ", " + y);
        collisionCheck();
        
    }

    public void update(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        update();
    }
    

    public void draw(Graphics g) {
        // System.out.println("Painting weapon");

        int loc_x = (int)offsetX + playerX;
        int loc_y = (int)offsetY + playerY;

        AffineTransform at = AffineTransform.getTranslateInstance(loc_x, loc_y);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);

        ((Graphics2D) g).drawImage(image, at, null);
        // ((Graphics2D) g).drawRect(loc_x, loc_y, width+5, height+5);

    }
}
