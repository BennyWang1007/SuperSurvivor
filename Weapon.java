import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

public abstract class Weapon {

    protected int x;
    protected int y;
    protected int offsetX;
    protected int offsetY;
    protected int playerX;
    protected int playerY;
    protected int FPS;
    protected GamePanel gamePanel;

    private int attack;
    protected HashMap<Integer, Integer> attackCooldowns;
    
    protected BufferedImage image;
    // private BufferedImage[] images; // for animation
    private BufferedImage originalImage;
    
    private int width;
    private int height;

    protected Player owner;
    protected Monster[] monsters; // maybe consider using a arraylist + hashmap to search by id
    protected int monsterCount;

    public Weapon(int width, int height, int attack, Player owner) {
        this.width = width;
        this.height = height;
        this.attack = attack;
        this.owner = owner;
        playerX = 0;
        playerY = 0;
        x = 0;
        y = 0;
        // loadAnimation();
        monsterCount = 0;
        attackCooldowns = new HashMap<Integer, Integer>();
        this.gamePanel = owner.getGamePanel();
        update();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        FPS = gamePanel.getFPS();
    }

    public void readImage(String imageName) {
        Image img = null;
        try {
            img = ImageIO.read(new File(imageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(img, 0, 0, width, height, null);
        image = originalImage;
    }

    public void setSize(int width, int height) {
        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
    }

    public void collisionCheck() {
        // System.out.println("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);
        monsterCount = gamePanel.getMonsterCount();
        monsters = gamePanel.getMonsters();
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].x < x + width && monsters[i].x + monsters[i].width > x && monsters[i].y < y + height && monsters[i].y + monsters[i].height > y) {
                if (attackCooldowns.containsKey(monsters[i].id)) {
                    continue;
                }
                attackCooldowns.put(monsters[i].id, FPS);
                monsters[i].damage(attack);
            }
        }
        // remove cooldowns <= 0
        Iterator<Integer> iterator = attackCooldowns.keySet().iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            attackCooldowns.put(id, attackCooldowns.get(id) - 1);
            if (attackCooldowns.get(id) <= 0) {
                iterator.remove();
            }
        }
    }

    public abstract void update();
    public abstract void draw(Graphics g);
    public abstract void loadAnimation();
}
