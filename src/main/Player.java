package main;

import java.awt.Graphics;

import monsters.*;
import weapons.*;

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
    private int speed;

    private GamePanel gamePanel;

    public Player(String name, int x, int y, int speed, GamePanel gamePanel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.attack = 20;
        this.gamePanel = gamePanel;
        this.weapons = new Weapon[1];
        this.weapons[0] = (Weapon)(new SpinningSword(100, 100, attack, 300, 100, this));
        // this.weapons[0].setGamePanel(gamePanel);
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        for (Weapon weapon : weapons) {
            weapon.setGamePanel(gamePanel);
        }
    }

    public void setMonsters(Monster[] monsters) {
        // for (Weapon weapon : weapons) {
        //     weapon.setMonsters(monsters);
        // }
    }
    public void setMonsterCount(int monsterCount) {
        // this.monsterCount = monsterCount;
        // for (Weapon weapon : weapons) {
        //     weapon.setMonsterCount(monsterCount);
        // }
    }

    public GamePanel getGamePanel() { return gamePanel; }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setName(String name) { this.name = name; }
    public void setPos(int x, int y) { this.x = x; this.y = y; }

    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    public void update() {
        for (Weapon weapon : weapons) {
            weapon.update();
        }
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
//        int x = this.x + this.width / 2;
//        int y = this.y + this.height / 2;
        int x = gamePanel.getWidth() / 2;
        int y = gamePanel.getHeight() / 2;
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
