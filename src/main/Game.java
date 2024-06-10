package main;

import javax.sound.sampled.FloatControl;
import javax.swing.*;

import entity.DropItem;
import entity.Entity;
import entity.Hitbox;
import entity.Player;
import entity.monster.Monster;
import listeners.GameKeyboardListener;
import listeners.GameMouseListener;
import weapons.*;

import java.util.*;
import java.io.*;

public class Game {

    public static final int FPS = 60;
    public static final double DELTA_TIME = 1. / FPS;
    private static final double NANO_TIME_PER_FRAME = 1000000000.0 / FPS;

    public static double gameTime = 0; // in seconds
    public static double monsterStrength = 1;

    public static ArrayList<ScoreEntry> scores;

    private static final boolean skipTitleScreen = false;

    // Window frame / panel
    final JFrame gameFrame;
    final GamePanel gamePanel;
    public final GameSettings settings;
    GameState gameState;
    private boolean inGame = false;

    // Sound
    Sound bgm;

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
    Player player;

    // Monsters
    private MonsterSpawner monsterSpawner;
    private int maxMonsterCount = 500;
    private Set<Monster> monsters;
    private int currentMonsterId = 0;

    private int monsterSpawnCooldown = FPS /6;
    private int monsterCooldownCounter = 0;

    // ExpOrbs
    private Set<DropItem> dropItems;

    // Projectiles
    private Set<Projectile> projectiles;

    // measured FPS & UPS
    private int measuredFPS = 0;

    // main
    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        // game settings
        settings = new GameSettings();
        bgm = new Sound("bgm.wav");

        // create the frame
        gameFrame = new JFrame("Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);

        // load scores
        loadScores();

        // mouse listener
        mouseListener = new GameMouseListener();

        // player
        player = new Player(this, "PlayerName", worldWidth/2, worldHeight/2);
        player.addBow();

        // monster
        monsters = new HashSet<>();
        monsterSpawner = new MonsterSpawner(this, player, monsters);

        // projectiles
        projectiles = new HashSet<>();

        // drop items
        dropItems = new HashSet<>();

        // keyboard listener
        keyboardListener = new GameKeyboardListener(this, player);

        // game panel
        gamePanel = new GamePanel(this, mouseListener);
        gamePanel.setPlayer(player);
        gamePanel.setMonsters(monsters);
        gamePanel.setDropItems(dropItems);
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
        setBgmVolume(settings.musicVolumeLevel);
        setSoundVolume(settings.soundVolumeLevel);
        bgm.loop();
        startGameLoop();

        // close the frame
        gameFrame.dispose();
    }

    public void init() {
        monsters.clear();
        currentMonsterId = 0;
        monsterCooldownCounter = 0;
        dropItems.clear();
        projectiles.clear();
        measuredFPS = 0;
        player.init();
        player.moveTo(worldWidth/2, worldHeight/2);
        player.addBow();
        gameTime = 0;
        monsterStrength = 1;
        gamePanel.init();
        inGame = false;
        keyboardListener.reset();
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
        keyboardListener.setPause(true);
    }

    public void resume() {
        gameState = GameState.MAIN_GAME;
        keyboardListener.setPause(false);
        inGame = true;
    }

    public void reset() {
        init();
        gameState = GameState.TITLE_SCREEN;
        inGame = false;
    }

    public boolean isInGame() {
        return inGame;
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

    public void playSound(SoundType soundType) {
        soundType.play();
    }

    public void setBgmVolume(int level) {
        bgm.setVolume(level);
    }

    public void setSoundVolume(int level) {
        SoundType.setVolume(level);
    }

    @SuppressWarnings("unchecked")
    private void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("scores.dat"))) {
            scores = (ArrayList<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            scores = new ArrayList<>();
        }
    }

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("scores.dat"))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addScoreEntry(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores, (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        saveScores();
    }

    private void endGame() {
        // set default name player.name in showInputDialog and add a discard button
        String name = JOptionPane.showInputDialog(gameFrame, "Game Over! Enter your name(cancel to discard)", player.name);
        
        if (name != null && !name.trim().isEmpty()) {
            addScoreEntry(name, player.getScore());
        }
        int choice = JOptionPane.showConfirmDialog(gameFrame, "Game Over! Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reset();
        } else {
            System.exit(0);
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
                deltaF = 0;
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
                monsterStrength = strengthFormula(gameTime);
            }

            // end game ?
            if (isOver()) {
                endGame();
            }
        }
    }

    private void processUpdate() {
        if (gameState == GameState.TITLE_SCREEN) return;
        if (gameState == GameState.PAUSE) return;
        if (gameState == GameState.LEVEL_UP) return;
        gameTime += DELTA_TIME;
        keyboardListener.update();
        player.update();
        dropItems.forEach(DropItem::update);
        monsters.forEach(Monster::update);
        projectiles.forEach(Projectile::update);
        processCollision();
        monsters.forEach(monster -> {
            if (monster.isDead()) {
                player.addScore((int)(monster.exp * (0.5 + Math.random() * 0.5)));
                monster.dropItems();
            }
        });
        monsters.removeIf(Monster::isDead);
        projectiles.removeIf(proj -> proj.toDelete);
        dropItems.removeIf(item -> item.isCollected);
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
            addMonster();
        } else {
            monsterCooldownCounter++;
        }
    }

    private void addMonster() {
        if (monsters.size() < maxMonsterCount) {
            currentMonsterId++;
            monsterSpawner.spawnMonster(currentMonsterId, monsterStrength);
        }
    }

    public void addDropItem(DropItem dropItem) {
        dropItems.add(dropItem);
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public double getMonsterStrength() {
        return strengthFormula(gameTime);
    }

    public double strengthFormula(double x) {
        double strengh = 0.03 * x + 1;
        if (x > 100) strengh += Math.pow(1.03, (x - 100));
        if (x > 200) strengh += 0.0002 * (x - 200) * (x - 200);
        return strengh;
    }

}