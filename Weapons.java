import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import javax.imageio.ImageIO;



public class Weapons {
    public float offsetX;
    public float offsetY;

    public BufferedImage image;
    public float degree;

    private int x;
    private int y;

    private BufferedImage originalImage;
    private float degreePerSecond;
    private int FPS;
    private float distance;
    private int width;
    private int height;

    
    private String imageName;
    private int degreeIndex;
    private BufferedImage[] images; // for animation


    public Weapons(String imageName, int distance, float degreePerSecond, int width, int height, int FPS) {
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
        // this.degreePerSecond = 1;
        this.degreePerSecond = degreePerSecond;
        this.FPS = FPS;
        this.images = new BufferedImage[360 / 3];
        x = 0;
        y = 0;
        // loadAnimation();
        update();
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

    public void update() {
        degree += degreePerSecond / FPS;
        if (degree >= 360) {
            degree -= 360;
        }
        // System.out.println("Degree: " + degree);
        offsetX = (float) (Math.cos(Math.toRadians(degree)) * distance);
        offsetY = (float) (Math.sin(Math.toRadians(degree)) * distance);
        
        // paintComponent(originalImage.getGraphics());
        degreeIndex = (360 - (int)(degree + 45) % 360) % 360;
        // image = images[degreeIndex / 3];
    }

    public void update(int x, int y) {
        this.x = x;
        this.y = y;
        update();
    }

    public void paintComponent(Graphics g) {

        // System.out.println("Painting weapon");

        // g.drawImage(images[degreeIndex / 3], (int)offsetX + x, (int)offsetY + y, null);
        // g.drawImage(image, (int)offsetX + x, (int)offsetY + y, null);
        // g.drawImage(originalImage, (int)offsetX + x, (int)offsetY + y, null);

        // image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degree), width / 2, height / 2);
        int loc_x = (int)offsetX + x;
        int loc_y = (int)offsetY + y;
        AffineTransform at = AffineTransform.getTranslateInstance(loc_x, loc_y);
        // float temdegree = degree + 45;
        float temdegree = degree;
        if (temdegree >= 360) {
            temdegree -= 360;
        } else if (temdegree < 0) {
            temdegree += 360;
        }
        at.rotate(Math.toRadians(temdegree), image.getWidth() / 2, image.getHeight() / 2);

        // // Resize the image

        ((Graphics2D) g).drawImage(image, at, null);

    }
}
