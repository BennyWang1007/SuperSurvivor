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

    public int hp;
    public int maxHp;
    public int attack;
    public int defense;

    public int damageCooldown;

    public Weapon[] weapons;
    private int speed;
    private int FPS;

    private GamePanel gamePanel;

    public Player(String name, int x, int y, int speed, GamePanel gamePanel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = 50;
        this.height = 50;
        this.speed = speed;
        this.attack = 20;
        this.hp = 100;
        this.maxHp = 100;
        this.defense = 0;
        this.damageCooldown = 0;
        this.gamePanel = gamePanel;
        this.FPS = gamePanel.getFPS();
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

    // public void setMonsters(Monster[] monsters) {
    //     // for (Weapon weapon : weapons) {
    //     //     weapon.setMonsters(monsters);
    //     // }
    // }
    // public void setMonsterCount(int monsterCount) {
    //     // this.monsterCount = monsterCount;
    //     // for (Weapon weapon : weapons) {
    //     //     weapon.setMonsterCount(monsterCount);
    //     // }
    // }

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
        damageCooldown--;
        for (Weapon weapon : weapons) {
            weapon.update();
        }
        if (damageCooldown > 0) return;
        int monsterCount = gamePanel.getMonsterCount();
        Monster[] monsters = gamePanel.getMonsters();
        int maxDamage = 0;
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].x - monsters[i].width / 2 < x + width / 2 && monsters[i].x + monsters[i].width / 2 > x - width / 2 &&
                monsters[i].y - monsters[i].height / 2 < y + height / 2 && monsters[i].y + monsters[i].height / 2 > y - height / 2) {
                maxDamage = Math.max(maxDamage, monsters[i].attack);
            }
        }
        if (maxDamage != 0) {
            damage(maxDamage);
            damageCooldown = FPS / 2;
        }
    }

    public void damage(int damage) {
        if (damage < 0) return;
        hp -= damage - defense;
        if (hp < 0) {
            hp = 0;
        }
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
        // int cx = this.x - width / 2;
        // int cy = this.y - height / 2;
        int cx = gamePanel.getWidth() / 2 - width / 2;
        int cy = gamePanel.getHeight() / 2 - height / 2;
        Weapon[] weapons = this.weapons;
        for (Weapon weapon : weapons) {
            weapon.draw(g);
        }
        g.drawRect(cx, cy, width, height);
        g.drawString(name, cx, cy - 5);
        
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = cx;
        int healthBarY = cy - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
        g.setColor(java.awt.Color.BLACK);
    }

}
