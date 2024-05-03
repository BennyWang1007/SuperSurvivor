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

    public Weapon[] weapons;
    private int monsterCount;
    private Monster[] monsters;
    private int speed;

    public Player(String name, int x, int y, int speed) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.weapons = new Weapon[1];
        this.weapons[0] = new Weapon("assets/sword_rotated", 100, 300, 100, 100, 1);
    }

    public void setMonsters(Monster[] monsters) {
        this.monsters = monsters;
        for (Weapon weapon : weapons) {
            weapon.setMonsters(monsters);
        }
    }
    public void setMonsterCount(int monsterCount) {
        this.monsterCount = monsterCount;
        for (Weapon weapon : weapons) {
            weapon.setMonsterCount(monsterCount);
        }
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setName(String name) { this.name = name; }

    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    public void update() {
        for (Weapon weapon : weapons) {
            weapon.update(x, y);
        }
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
        int x = this.x + this.width / 2;
        int y = this.y + this.height / 2;
        int width = this.width;
        int height = this.height;
        Weapon[] weapons = this.weapons;
        for (Weapon weapon : weapons) {
            weapon.draw(g);
        }
        g.drawRect(x, y, width, height);
        g.drawString(name, x, y - 5);
    }

}
