package main;

import javax.swing.JPanel;
import java.awt.*;

import monsters.*;
import weapons.*;

public class GamePanel extends JPanel{

    private Player player;
    private int currentMonsterId;

    private int monsterCount;
    private int maxMonsterCount;
    private Monster[] monsters;

    private int panelHeight;
    private int panelWidth;

    private int FPS = 60;
    private boolean isPause = false;

    public GamePanel(Player player) {
        super();
        this.player = player;
        currentMonsterId = 0;
        monsterCount = 0;
        maxMonsterCount = 100;
        monsters = new Monster[maxMonsterCount];
        // player.setMonsters(monsters);
    }

    public GamePanel() {
        super();
        monsterCount = 0;
        maxMonsterCount = 100;
        monsters = new Monster[maxMonsterCount];
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
        int randX = (int)(Math.random() * panelWidth);
        int randY = (int)(Math.random() * panelHeight);
        player.setPos(randX, randY);
        // player.setMonsters(monsters);
        for (int i = 0; i < 5; i++) {
            randX = (int)(Math.random() * panelWidth);
            randY = (int)(Math.random() * panelHeight);
            addMonster(new Monster("Monster", randX, randY, 100, 20, 1, player));
        }
    }

    public void addMonster(Monster monster) {
        if (monsterCount < maxMonsterCount) {
            currentMonsterId++;
            // System.out.println("Adding monster with id " + currentMonsterId);
            monster.setId(currentMonsterId);
            monsters[monsterCount] = monster;
            monsterCount++;
            // player.setMonsterCount(monsterCount);
        }
    }

    public void removeMonster(Monster monster) {
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
    public Monster[] getMonsters() { return monsters; }
    public void setFPS(int FPS) { this.FPS = FPS; }

    public void update() {
        if (isPause) {
            return;
        }
        panelHeight = this.getHeight();
        panelWidth = this.getWidth();

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
                monsters[i] = new Monster("Monster", randX, randY, 100, 20, 1, player);
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
        // for (Weapon weapon : player.weapons) {
        //     weapon.draw(g);
        // }
        player.draw(g);
    }
}
