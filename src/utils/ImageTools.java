package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageTools {

    public static BufferedImage readImage(String resourcePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getResourceInputStream(resourcePath));
        } catch (IOException e) {
            System.err.println("Error occurred while reading image with path " + resourcePath);
        } catch (NullPointerException e) {
            System.err.println("No such file " + resourcePath);
        }
        return image;
    }

    public static BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(originalImage, 0, 0, width, height, null);
        g2.dispose();
        return scaledImage;
    }

    public static BufferedImage rotateImage(BufferedImage originalImage, float degree) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degree), width/2., height/2.);
        BufferedImage rotatedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = rotatedImage.createGraphics();
        g.drawImage(originalImage, at, null);
        g.dispose();
        return rotatedImage;
    }

    public static BufferedImage mirrorImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage mirroredImage = new BufferedImage(width, height, originalImage.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mirroredImage.setRGB(width-x-1, y, originalImage.getRGB(x, y));
            }
        }
        return mirroredImage;
    }

    private static InputStream getResourceInputStream(String resourcePath) {
        return ImageTools.class.getResourceAsStream(resourcePath);
    }

}
