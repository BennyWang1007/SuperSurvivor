package main;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import entity.*;
import entity.monster.Monster;
import listeners.GameMouseListener;

public class GamePanel extends JPanel{

    private final Game game;
    private Player player;
    private Set<Monster> monsters;
    private Image backgroundImage;
    private int mapWidth = 3000;
    private int mapHeight = 3000;

    private final GameMouseListener mouseListener;
    private final TitleScreen titleScreen;
    private Font cubicFont;

    public GamePanel(Game game, GameMouseListener mouseListener) {
        super();
        this.game = game;
        this.mouseListener = mouseListener;
        this.titleScreen = new TitleScreen(game, this, mouseListener);
        setupFont();
        setupScreenSize(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        setBackgroundImage("res/backgnd.png");
    }

    private void setupFont() {
        try {
            InputStream is = new FileInputStream("res/font/Cubic_11_1.100_R.ttf");
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
        setDoubleBuffered(true);
    }

    private void setBackgroundImage(String imageName) {
        Image img = null;
        try {
            img = Toolkit.getDefaultToolkit().getImage(imageName);
        } catch (Exception e) {
            System.out.println("Error loading image: " + imageName);
        }
        // scale the image to map size
        backgroundImage = img.getScaledInstance(mapWidth, mapHeight, Image.SCALE_DEFAULT);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setMonsters(Set<Monster> monsters) {
        this.monsters = monsters;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(cubicFont);
        g.setFont(g.getFont().deriveFont(12f));

        GameState gameState = game.getGameState();

        if (gameState == GameState.TITLE_SCREEN) {
            titleScreen.draw(g);
        } else if (gameState == GameState.MAIN_GAME || gameState == GameState.PAUSE) {
            // drawBackground(g);
            // draw : monster -> weapon -> player
            drawMonsters(g);
            drawPlayer(g);
            drawFPS(g);
            if (gameState == GameState.PAUSE) {
                drawPauseView(g);
            }
        }

        g.dispose();
    }

    private void drawBackground(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // find the part of the background to draw
        int sx = (int)(mapWidth / 2 + player.x - panelWidth / 2), ex = (int)(mapWidth / 2 + player.x + panelWidth / 2);
        int sy = (int)(mapHeight / 2 + player.y - panelHeight / 2), ey = (int)(mapHeight / 2 + player.y + panelHeight / 2);
        // g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, sx, sy, ex, ey, this);

        // draw the background with opacity 0.5
        BufferedImage bimg = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = bimg.createGraphics();
        bg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        bg.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, sx, sy, ex, ey, this);
        bg.dispose();
        g.drawImage(bimg, 0, 0, this);
    }

    private void drawPauseView(Graphics g) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // print pause at the middle of screen with big font with red color
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSE", panelWidth / 2 - 100, panelHeight / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void drawMonsters(Graphics g) {
        monsters.forEach(monster -> monster.draw(g));
    }

    private void drawPlayer(Graphics g) {
        player.draw(g);
    }

    private void drawFPS(Graphics g) {
        g.setColor(Color.BLACK);
        String str = String.format("FPS: %d", game.getMeasuredFPS());
        g.drawString(str, 0, 10);
    }
    
}
