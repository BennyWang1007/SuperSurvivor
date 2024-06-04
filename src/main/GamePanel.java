package main;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import entity.*;
import entity.monster.Monster;
import listeners.GameMouseListener;

public class GamePanel extends Canvas {

    private final Game game;
    private Player player;
    private Set<Monster> monsters;
    private Set<ExpOrb> exps;

    private final TileManager tileManager;

    private final GameMouseListener mouseListener;
    private final TitleScreen titleScreen;
    private Font cubicFont;
    
    private static final boolean DEBUG = true;

    public GamePanel(Game game, GameMouseListener mouseListener) {
        super();
        this.game = game;
        this.mouseListener = mouseListener;
        this.tileManager = new TileManager(game, this, player);
        this.titleScreen = new TitleScreen(game, this, mouseListener);
        setupFont();
        setupScreenSize(game.screenWidth, game.screenHeight);
    }

    private void setupFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/Cubic_11_1.100_R.ttf");
            cubicFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setupScreenSize(int width, int height) {
        Dimension screenSize = new Dimension(width, height);
        setMinimumSize(screenSize);
        setPreferredSize(screenSize);
        setMaximumSize(screenSize);
    }

    public void setPlayer(Player player) { this.player = player; }
    public void setMonsters(Set<Monster> monsters) { this.monsters = monsters; }
    public void setExpOrbs(Set<ExpOrb> exps) { this.exps = exps; }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null){
            createBufferStrategy(4);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setFont(cubicFont);
        g.setFont(g.getFont().deriveFont(12f));

        GameState gameState = game.getGameState();

        if (gameState == GameState.TITLE_SCREEN) {
            titleScreen.draw(g);
        } else if (gameState == GameState.MAIN_GAME || gameState == GameState.PAUSE) {
            drawBackground(g);
            // draw : monster -> weapon -> player
            drawMonsters(g);
            drawExp(g);
            drawPlayer(g);
            drawDamageReceived(g);
            if (DEBUG) {
                // draw a rectangle as background of debug info
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 260, 100);
                // draw the position of player
                g.setColor(Color.WHITE);
                g.setFont(getFont().deriveFont(20.0f));
                g.drawString("Player: " + (int)player.x + ", " + (int)player.y, 10, 35);
                g.drawString("Exp: " + player.exp + "/" + player.expTable[player.level] + ", Level: " + player.level, 10, 55);
                g.setFont(getFont().deriveFont(12.0f));
            }
            drawFPS(g);

            if (gameState == GameState.PAUSE) {
                drawPauseView(g);
            }
        }
        g.dispose();
        bs.show();
    }

    private void drawBackground(Graphics g) {
        tileManager.draw(g);
    }

    private void drawPauseView(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // print pause at the middle of screen with big font with red color
        g.setColor(Color.RED);
        g.setFont(getFont().deriveFont(50.0f));
        g.drawString("PAUSE", panelWidth / 2 - 100, panelHeight / 2);
        g.setFont(getFont().deriveFont(12.0f));
    }

    private void drawMonsters(Graphics g) {
        monsters.forEach(monster -> monster.draw(g));
    }

    private void drawExp(Graphics g) {
        exps.forEach(exp -> exp.draw(g));
    }

    private void drawDamageReceived(Graphics g) {
        monsters.forEach(monster -> monster.drawDamageReceived(g));
    }

    private void drawPlayer(Graphics g) {
        player.draw(g);
    }

    private void drawFPS(Graphics g) {
        g.setColor(Color.BLACK);
        String str = String.format("FPS: %d", game.getMeasuredFPS());
        g.setColor(Color.GREEN);
        g.drawString(str, 10, 15);
    }
    
}
