import javax.swing.JPanel;
import java.awt.Graphics;

public class Player {
    public String name;
    public int x;
    public int y;
    public int width;
    public int height;

    public int health;
    public int maxHealth;
    public int attack;
    public int defense;

    public Weapons[] weapons;
    private int speed;

    public Player(String name, int x, int y, int speed) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.weapons = new Weapons[1];
        this.weapons[0] = new Weapons("assets/sword_rotated", 100, 300, 100, 100, 60);
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setName(String name) { this.name = name; }

    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    public void update() {
        for (Weapons weapon : weapons) {
            weapon.update(x, y);
        }
    }

}
