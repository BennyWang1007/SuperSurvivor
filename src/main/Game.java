package main;

import javax.swing.*;

import entity.Entity;
import entity.ExpOrb;
import entity.Hitbox;
import entity.Player;
import entity.monster.Monster;
import listeners.GameKeyboardListener;
import listeners.GameMouseListener;
import weapons.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    public static final int FPS = 60;
    public static final double DELTA_TIME = 1. / FPS;
    private static final double NANO_TIME_PER_FRAME = 1000000000.0 / FPS;

    private static final boolean skipTitleScreen = false;

    // Window frame / panel
    private final JFrame gameFrame;
    private final GamePanel gamePanel;
    private GameState gameState;

    // Screen
    private final int originalTileSize = 16;
    private final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 24;
    public final int maxScreenRow = 15;
    public int screenCenterX;
    public int screenCenterY;
    public final int screenWidth = maxScreenCol * tileSize;
    public final int screenHeight = maxScreenRow * tileSize;

    // Map
    public final int maxWorldRow = 100;
    public final int maxWorldCol = 100;
    public final int worldWidth = maxWorldCol * tileSize;
    public final int worldHeight = maxWorldRow * tileSize;

    // Listener
    public final GameKeyboardListener keyboardListener;
    public final GameMouseListener mouseListener;

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

    // Projectiles
    private Set<Projectile> projectiles;

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

        // player
        player = new Player(this, "PlayerName", worldWidth/2, worldHeight/2, 250);
        player.addBow();

        // monster
        monsters = new HashSet<>();
        monsterSpawner = new MonsterSpawner(this, player, monsters);

        // projectiles
        projectiles = new HashSet<>();

        // exp orbs
        exps = new HashSet<>();

        // keyboard listener
        keyboardListener = new GameKeyboardListener(this, player);

        // game panel
        gamePanel = new GamePanel(this, mouseListener);
        gamePanel.setPlayer(player);
        gamePanel.setMonsters(monsters);
        gamePanel.setExpOrbs(exps);
        gamePanel.setProjectiles(projectiles);
        gamePanel.addMouseListener(mouseListener);
        gamePanel.addMouseMotionListener(mouseListener);
        gamePanel.addKeyListener(keyboardListener);
        gameFrame.add(gamePanel);
        gameState = GameState.TITLE_SCREEN;
        if (skipTitleScreen) { gameState = GameState.MAIN_GAME; }

        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        gamePanel.initLevelUpChoices();
        gamePanel.setMap(1);
        // game loop
        startGameLoop();

        // close the frame
        gameFrame.dispose();
    }

    public int getMeasuredFPS() {
        return measuredFPS;
    }

    public Set<Monster> getMonsters() {
        return monsters;
    }

    public boolean isOver() {
        return player.hp <= 0;
    }

    public void calculateCenter() {
        screenCenterX = getCenterX();
        screenCenterY = getCenterY();
    }

    public int getCenterX() {
        if (player.x < gamePanel.getWidth() / 2) {
            return gamePanel.getWidth() / 2;
        } else if (player.x > worldWidth - gamePanel.getWidth() / 2) {
            return worldWidth - gamePanel.getWidth() / 2;
        } else {
            return (int) player.x;
        }
    }

    public int getCenterY() {
        if (player.y < gamePanel.getHeight() / 2) {
            return gamePanel.getHeight() / 2;
        } else if (player.y > worldHeight - gamePanel.getHeight() / 2) {
            return worldHeight - gamePanel.getHeight() / 2;
        } else {
            return (int) player.y;
        }
    }

    public int translateToScreenX(float worldX) {
        return (int) (worldX - screenCenterX + gamePanel.getWidth()/2);
    }

    public int translateToScreenY(float worldY) {
        return (int) (worldY - screenCenterY + gamePanel.getHeight()/2);
    }

    public float validatePositionX(float x) {
        return Math.min(Math.max(x, 0), worldWidth);
    }

    public float validatePositionY(float y) {
        return Math.min(Math.max(y, 0), worldHeight);
    }

    public boolean isValidPosition(float x, float y) {
        // TODO: check if the position is valid
        return x >= 0 && x <= worldWidth && y >= 0 && y <= worldHeight;
    }

    public boolean isInScreen(float x, float y) {
        return x >= screenCenterX - gamePanel.getWidth() / 2 && x <= screenCenterX + gamePanel.getWidth() / 2
                && y >= screenCenterY - gamePanel.getHeight() / 2 && y <= screenCenterY + gamePanel.getHeight() / 2;
    }

    public void pause() {
        gameState = GameState.PAUSE;
    }

    public void resume() {
        gameState = GameState.MAIN_GAME;
    }

    public void levelUp() {
        gameState = GameState.LEVEL_UP;
    }

    public void quit() {
        // TODO: Quit Game
        System.exit(0);
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameMouseListener getMouseListener() {
        return mouseListener;
    }

    public int[][] getMapTileNum() {
        return gamePanel.gameMap.mapTileNum;
    }

    public MapTile[] getMapTiles() {
        return gamePanel.gameMap.tile;
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
        if (gameState == GameState.LEVEL_UP) return;
        keyboardListener.update();
        player.update();
        exps.forEach(ExpOrb::update);
        monsters.forEach(Monster::update);
        projectiles.forEach(Projectile::update);
        processCollision();
        monsters.forEach(monster -> {
            if (monster.isDead()) {
                addExpOrb(new ExpOrb(this, monster.x, monster.y, monster.exp, player));
            }
        });
        monsters.removeIf(Monster::isDead);
        projectiles.removeIf(proj -> proj.toDelete);
        exps.removeIf(exp -> exp.isCollected);
        processMonsterSpawn();
        calculateCenter();
    }

    private void processFrame() {
        gamePanel.render();
    }

    private void processCollision() {
        monsters.forEach(monster -> {
            // monster hit player
            if (isCollided(player, monster)) {
                player.collideWith(monster);
            }

            // player's weapon hit monster
            player.getWeapons().forEach(weapon -> {
                if (isCollided(weapon, monster)) {
                    weapon.attackOn(monster);
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

    
    public void addExpOrb(ExpOrb expOrb) {
        exps.add(expOrb);
    }

    public void removeExpOrb(ExpOrb expOrb) {
        exps.remove(expOrb);
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

}