package main;

import javax.swing.*;

import api.EventListener;
import entity.Entity;
import entity.ExpOrb;
import entity.Hitbox;
import entity.Player;
import entity.monster.Monster;
import event.EnemyHitPlayerEvent;
import event.EventDispatcher;
import event.WeaponHitEnemyEvent;
import listeners.GameKeyboardListener;
import listeners.GameMouseListener;
import listeners.PlayerAttackListener;
import listeners.PlayerHurtListener;
import weapons.Weapon;

import java.util.HashSet;
import java.util.Set;

public class Game {

    public static final int FPS = 120;
    public static final double DELTA_TIME = 1. / FPS;
    public static final int SCREEN_WIDTH = 1080;
    public static final int SCREEN_HEIGHT = 720;
    private static final double NANO_TIME_PER_FRAME = 1000000000.0 / FPS;

    // Window frame / panel
    private final JFrame gameFrame;
    private final GamePanel gamePanel;
    private GameState gameState;

    // Map
    private int mapWidth = 2000;
    private int mapHeight = 2000;
    public int mapCenterX;
    public int mapCenterY;

    // Listener
    private final GameKeyboardListener keyboardListener;
    private final GameMouseListener mouseListener;

    // Player
    private Player player;

    // Monsters
    private MonsterSpawner monsterSpawner;
    private int maxMonsterCount = 500;
    private Set<Monster> monsters;
    private int currentMonsterId = 0;

    private int monsterSpawnCooldown = FPS /6;
    private int monsterCooldownCounter = 0;

    // ExpOrbs
    private Set<ExpOrb> exps;

    // Event
    private final EventDispatcher eventDispatcher;

    // measured FPS & UPS
    private int measuredFPS = 0;

    // main
    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        // create the frame
        gameFrame = new JFrame("Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        
        // mouse listener
        mouseListener = new GameMouseListener();
        gameFrame.addMouseListener(mouseListener);
        gameFrame.addMouseMotionListener(mouseListener);

        // game panel
        gamePanel = new GamePanel(this, mouseListener);
        gameFrame.add(gamePanel);
        gameState = GameState.TITLE_SCREEN;

        // player
        // NOTE: original one will always be 0, 0.
        // player = new Player(this, "PlayerName", gamePanel.getWidth()/2, gamePanel.getHeight()/2, 250);
        player = new Player(this, "PlayerName", mapWidth/2, mapHeight/2, 250);
        gamePanel.setPlayer(player);

        // monster
        monsters = new HashSet<>();
        gamePanel.setMonsters(monsters);
        monsterSpawner = new MonsterSpawner(this, player, monsters);

        // exp orbs
        exps = new HashSet<>();
        gamePanel.setExpOrbs(exps);

        // keyboard listener
        keyboardListener = new GameKeyboardListener(this, player);
        gameFrame.addKeyListener(keyboardListener);

        eventDispatcher = new EventDispatcher();
        registerEventListener(new PlayerHurtListener());
        registerEventListener(new PlayerAttackListener());

        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        // game loop
        startGameLoop();

        // close the frame
        gameFrame.dispose();
    }

    public int getMeasuredFPS() {
        return measuredFPS;
    }

    public boolean isOver() {
        return player.hp <= 0;
    }

    public void calculateCenter() {
        mapCenterX = getCenterX();
        mapCenterY = getCenterY();
    }

    public int getCenterX() {
        if (player.x < gamePanel.getWidth() / 2) {
            return gamePanel.getWidth() / 2;
        } else if (player.x > mapWidth - gamePanel.getWidth() / 2) {
            return mapWidth - gamePanel.getWidth() / 2;
        } else {
            return (int) player.x;
        }
    }

    public int getCenterY() {
        if (player.y < gamePanel.getHeight() / 2) {
            return gamePanel.getHeight() / 2;
        } else if (player.y > mapHeight - gamePanel.getHeight() / 2) {
            return mapHeight - gamePanel.getHeight() / 2;
        } else {
            return (int) player.y;
        }
    }

    public int translateToScreenX(float worldX) {
        return (int) (worldX - mapCenterX + gamePanel.getWidth()/2);
    }

    public int translateToScreenY(float worldY) {
        return (int) (worldY - mapCenterY + gamePanel.getHeight()/2);
    }

    public float validatePositionX(float x) {
        return Math.min(Math.max(x, 0), mapWidth);
    }

    public float validatePositionY(float y) {
        return Math.min(Math.max(y, 0), mapHeight);
    }

    public boolean isValidatePosition(float x, float y) {
        // TODO: check if the position is valid
        return x >= 0 && x <= mapWidth && y >= 0 && y <= mapHeight;
    }

    public void pause() {
        gameState = GameState.PAUSE;
    }

    public void resume() {
        gameState = GameState.MAIN_GAME;
    }

    public void quit() {
        // TODO: Quit Game
        System.exit(0);
    }

    public GameState getGameState() {
        return gameState;
    }

    private void registerEventListener(EventListener listener) {
        try {
            eventDispatcher.registerEventListener(listener);
        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startGameLoop() {
        // For actual game update
        long previousTime = System.nanoTime();
        int frames = 0;

        double deltaF = 0;

        // For measure
        long previousTimeMillis = System.currentTimeMillis();

        // Game loop
        while (true) {
            // game update
            long currentTime = System.nanoTime();

            deltaF += (currentTime - previousTime) / NANO_TIME_PER_FRAME;
            previousTime = currentTime;

            if (deltaF >= 1) {
                deltaF--;
                frames++;
                processUpdate();
                processFrame();
            }

            // measure
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - previousTimeMillis >= 1000) {
                previousTimeMillis = currentTimeMillis;
                measuredFPS = frames;
                frames = 0;
            }

            // end game ?
            if (isOver()) {
                break;
            }
        }
    }

    private void processUpdate() {
        if (gameState == GameState.TITLE_SCREEN) return;
        if (gameState == GameState.PAUSE) return;
        keyboardListener.update();
        player.update();
        exps.forEach(ExpOrb::update);
        while (player.levelUp > 0) {
            player.levelUp--;
            levelUp();
        }
        player.getWeapons().forEach(Weapon::update);
        monsters.forEach(Monster::update);
        processCollision();
        monsters.forEach(monster -> {
            if (monster.isDead()) {
                addExpOrb(new ExpOrb(this, monster.x, monster.y, monster.exp, player));
            }
        });
        monsters.removeIf(Monster::isDead);
        exps.removeIf(exp -> exp.isCollected);
        processMonsterSpawn();
        calculateCenter();
    }

    private void processFrame() {
        gamePanel.repaint();
    }

    private void processCollision() {
        monsters.forEach(monster -> {
            // monster hit player
            if (isCollided(player, monster)) {
                eventDispatcher.dispatchEvent(new EnemyHitPlayerEvent(player, monster));
            }

            // player's weapon hit monster
            player.getWeapons().forEach(weapon -> {
                if (isCollided(weapon, monster)) {
                    eventDispatcher.dispatchEvent(new WeaponHitEnemyEvent(weapon, monster));
                }
            });
        });
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

    private void processMonsterSpawn() {
        if (monsterCooldownCounter >= monsterSpawnCooldown) {
            monsterCooldownCounter -= monsterSpawnCooldown;
            addMonster(20);
        } else {
            monsterCooldownCounter++;
        }
    }

    private void addMonster(int exp) {
        if (monsters.size() < maxMonsterCount) {
            currentMonsterId++;
            monsterSpawner.spawnMonster(currentMonsterId, exp);
        }
    }

    public void levelUp() {
        // TODO: level up player
        player.maxHp += 10;
        player.hp = player.maxHp;
        player.attack += 5;
        player.defense += 2;
    }

    
    public void addExpOrb(ExpOrb expOrb) {
        exps.add(expOrb);
    }

    public void removeExpOrb(ExpOrb expOrb) {
        exps.remove(expOrb);
    }

}