package main;

import javax.swing.JPanel;

import java.awt.*;

import api.*;
import entity.*;
import entity.enemy.Enemy;
import event.*;
import listeners.*;
import weapons.*;

public class GamePanel extends JPanel{

    private Player player;
    private int currentMonsterId;

    private int monsterCount;
    private int maxMonsterCount;
    private Enemy[] monsters;

    private int panelHeight;
    private int panelWidth;

    private int FPS = 60;
    private boolean isPause = false;

    private Image backgroundImage;
    private int mapWidth;
    private int mapHeight;


    private int frameCount = 0;
    private int updateCount = 0;
    private int measuredFPS = 0;
    private int measuredUPS = 0;

    private final EventDispatcher eventDispatcher;

    // for testing
    public final int renderMode = 0; // 0: event, 1: hierarchy

    public GamePanel() {
        super();
        currentMonsterId = 0;
        monsterCount = 0;
        maxMonsterCount = 100;
        monsters = new Enemy[maxMonsterCount];
        mapWidth = 3000;
        mapHeight = 3000;
        setBackgroundImage("res/backgnd.png");
        if (renderMode == 0) {
            eventDispatcher = new EventDispatcher();
            registerEventListener(new PlayerHurtListener());
            registerEventListener(new PlayerAttackListener());
        } else {
            eventDispatcher = null;
        }
    }

    public GamePanel(int FPS) {
        this();
        this.FPS = FPS;
    }

    public void setBackgroundImage(String imageName) {
        Image img = null;
        try {
            img = Toolkit.getDefaultToolkit().getImage(imageName);
        } catch (Exception e) {
            System.out.println("Error loading image: " + imageName);
        }
        // scale the image to map size
        backgroundImage = img.getScaledInstance(mapWidth, mapHeight, Image.SCALE_DEFAULT);
        // backgroundImage = img;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.setGamePanel(this);
        // player.setMonsters(monsters);
    }

    public void initGame() {
        panelHeight = this.getHeight();
        panelWidth = this.getWidth();
        System.out.println("Panel size: " + panelWidth + ", " + panelHeight);
        player.setPos(mapWidth / 4, mapHeight / 4);
        // player.setMonsters(monsters);

        int randX = (int)(Math.random() * panelWidth);
        int randY = (int)(Math.random() * panelHeight);
        for (int i = 0; i < 5; i++) {
            addMonster(new Enemy("Monster", randX, randY, 100, 20, 1, player));
            randX = (int)(Math.random() * panelWidth);
            randY = (int)(Math.random() * panelHeight);
        }
    }

    public void addMonster(Enemy monster) {
        if (monsterCount < maxMonsterCount) {
            currentMonsterId++;
            // System.out.println("Adding monster with id " + currentMonsterId);
            monster.setId(currentMonsterId);
            monsters[monsterCount] = monster;
            monsterCount++;
            // player.setMonsterCount(monsterCount);
        }
    }

    public void removeMonster(Enemy monster) {
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i] == monster) {
                for (int j = i; j < monsterCount - 1; j++) {
                    monsters[j] = monsters[j + 1];
                }
                monsterCount--;
                // player.setMonsterCount(monsterCount);
                break;
            }
        }
    }

    public int getFPS() { return FPS; }
    public int getMonsterCount() { return monsterCount; }
    public Enemy[] getMonsters() { return monsters; }
    public void setFPS(int FPS) { this.FPS = FPS; }

    private long prevTime = 0;

    public void measuredFPSandUPS() {
        long currentTime = System.nanoTime();
        if (currentTime - prevTime >= 1000000000) {
            measuredFPS = frameCount;
            measuredUPS = updateCount;
            frameCount = 0;
            updateCount = 0;
            prevTime = currentTime;
        }

        frameCount++;
        updateCount++;
    }

    public void update() {
        if (isPause) {
            return;
        }
        measuredFPSandUPS();
        panelHeight = this.getHeight();
        panelWidth = this.getWidth();

        if (renderMode == 0) {
            processCollision();
        };

        player.update();
        for (Weapon weapon : player.weapons) {
            weapon.update();
        }
        for (int i = 0; i < monsterCount; i++) {
            monsters[i].update();
        }

        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].isDead()) {
                // removeMonster(monsters[i]);
                int randX = (int)(Math.random() * panelWidth);
                int randY = (int)(Math.random() * panelHeight);
                currentMonsterId++;
                monsters[i] = new Enemy("Monster", randX, randY, 100, 20, 1, player);
                monsters[i].setId(currentMonsterId);
            }
        }
    }

    public boolean isGameOver() {
        // TODO
        return player.hp <= 0;
    }

    public void reversePause() {
        isPause = !isPause;
    }

    public void setPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // System.out.println("Painting player");

        // find the part of the image to draw
        int sx = (int)(mapWidth / 2 + player.x - panelWidth / 2), ex = (int)(mapWidth / 2 + player.x + panelWidth / 2);
        int sy = (int)(mapHeight / 2 + player.y - panelHeight / 2), ey = (int)(mapHeight / 2 + player.y + panelHeight / 2);
        g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, sx, sy, ex, ey, this);

        if (isPause) {
            // print pause at the middle of screen with big font with red color
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSE", panelWidth / 2 - 100, panelHeight / 2);
            g.setColor(Color.BLACK);
            // return;
        }
        
        // draw : monster -> weapon -> player
        for (int i = 0; i < monsterCount; i++) {
            monsters[i].draw(g);
        }
        player.draw(g);
        drawFPSAndUPS(g);
    }

    private void drawFPSAndUPS(Graphics g) {
        String str = String.format("FPS: %d | UPS: %d", measuredFPS, measuredUPS);
        g.drawString(str, 0, 10);
    }

    private void registerEventListener(EventListener listener) {
        try {
            eventDispatcher.registerEventListener(listener);
        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processCollision() {
        for (int i = 0; i < monsterCount; i++) {
            if (isCollided(player, monsters[i])) {
                eventDispatcher.dispatchEvent(new EnemyHitPlayerEvent(player, monsters[i]));
            }
            for (Weapon weapon : player.weapons) {
                if (isCollided(weapon, monsters[i])) {
                    eventDispatcher.dispatchEvent(new WeaponHitEnemyEvent(weapon, monsters[i]));
                }
            }
        }
        // enemies.forEach(enemy -> {
        //     if (isCollided(player, enemy)) {
        //         eventDispatcher.dispatchEvent(new EnemyHitPlayerEvent(player, enemy));
        //     }
        // });
    }

    private boolean isCollided(Entity e1, Entity e2) {
        Hitbox b1 = e1.getHitBox();
        Hitbox b2 = e2.getHitBox();
        if (b1.startX < b2.startX && b1.endX < b2.startX) return false;
        if (b2.startX < b1.startX && b2.endX < b1.startX) return false;
        if (b1.startY < b2.startY && b1.endY < b2.startY) return false;
        if (b2.startY < b1.startY && b2.endY < b1.startY) return false;
        return true;
    }

    
}
