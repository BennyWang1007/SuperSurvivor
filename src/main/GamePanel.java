package main;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
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
    private Font cubicFont;
    private Stroke borderStroke;
    private Color normalColor = Color.WHITE;
    private Color hoverColor = Color.LIGHT_GRAY;
    private Color clickColor = Color.GRAY;

    private final String screenTitle = "超級倖存者";

    public GamePanel(Game game, GameMouseListener mouseListener) {
        super();
        this.game = game;
        this.mouseListener = mouseListener;
        borderStroke = new BasicStroke(3);
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
        GameState gameState = game.getGameState();

        if (gameState == GameState.TITLE_SCREEN) {
            drawTitleScreen(g);
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

    private void drawTitleScreen(Graphics g) {
        g.setFont(cubicFont);
        g.setFont(g.getFont().deriveFont(96f));

        Rectangle2D rect;
        Color textColor = Color.BLACK;
        Color borderColor = Color.BLACK;
        Color backColor = normalColor;
        Color shadowColor = Color.DARK_GRAY;
        boolean shadow = false;

        // MAIN TITLE SHADOW
        int x = getXForCenterText(g, screenTitle);
        int y = getHeight()/4;
        g.setColor(Color.GRAY);
        g.drawString(screenTitle, x+3, y+3);
        g.setColor(Color.BLACK);

        // MAIN TITLE
        g.drawString(screenTitle, x, y);

        // START GAME
        g.setFont(g.getFont().deriveFont(48f));
        String textStart = "開始遊戲";
        int gap = 20;
        int textHeight = (int) g.getFontMetrics().getStringBounds(textStart, g).getHeight();
        x = getXForCenterText(g, textStart);
        y = getHeight()*4/7;
        rect = getTextRectangle(g, textStart, x, y);
        if (isClicked(rect)) {
            backColor = clickColor;
            shadow = true;
            game.resume();
        } else if (isHover(rect)) {
            backColor = hoverColor;
            shadow = false;
        } else {
            backColor = normalColor;
            shadow = false;
        }
        drawTextBounds(g, rect, borderColor, backColor, shadowColor, shadow);
        g.setColor(textColor);
        g.drawString(textStart, x, y);

        // CHOOSE LEVEL
        String textLevel = "選擇關卡";
        x = getXForCenterText(g, textLevel);
        y += textHeight + gap;
        rect = getTextRectangle(g, textLevel, x, y);
        if (isClicked(rect)) {
            backColor = clickColor;
            shadow = true;
        } else if (isHover(rect)) {
            backColor = hoverColor;
            shadow = false;
        } else {
            backColor = normalColor;
            shadow = false;
        }
        drawTextBounds(g, rect, borderColor, backColor, shadowColor, shadow);
        g.setColor(textColor);
        g.drawString(textLevel, x, y);

        // QUIT GAME
        String textQuit = "離開遊戲";
        x = getXForCenterText(g, textQuit);
        y += textHeight + gap;
        rect = getTextRectangle(g, textQuit, x, y);
        if (isClicked(rect)) {
            backColor = clickColor;
            shadow = true;
        } else if (isHover(rect)) {
            backColor = hoverColor;
            shadow = false;
        } else {
            backColor = normalColor;
            shadow = false;
        }
        drawTextBounds(g, rect, borderColor, backColor, shadowColor, shadow);
        g.setColor(textColor);
        g.drawString(textQuit, x, y);
    }

    private void drawTextBounds(Graphics g, Rectangle2D rect, Color borderColor, Color backgroundColor, Color shadowColor, boolean shadow) {
        Stroke originalStroke = ((Graphics2D)g).getStroke();
        int width = (int) rect.getWidth();
        int height = (int) rect.getHeight();
        int x = (int) rect.getX();
        int y = (int) rect.getY() - height/2;
        if (shadow) {
            g.setColor(shadowColor);
            g.fillRect(x+4, y+4, width, height);
        }
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);
        g.setColor(borderColor);
        ((Graphics2D) g).setStroke(borderStroke);
        g.drawRect(x, y, width, height);
        ((Graphics2D) g).setStroke(originalStroke);
    }

    private Rectangle2D getTextRectangle(Graphics g, String text, int x, int y) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        int horzPadding = 75;
        int width = (int) rect.getWidth() + 2*horzPadding;
        int height = (int) rect.getHeight();
        x = x - horzPadding;
        y = (int) (y - rect.getHeight()*23/100.);
        rect.setRect(x, y, width, height);
        return rect;
    }

    private boolean isHover(Rectangle2D rect) {
        return rect.contains(mouseListener.mouseX, mouseListener.mouseY);
    }

    private boolean isClicked(Rectangle2D rect) {
        return mouseListener.mouseClicked && isHover(rect);
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

    private int getXForCenterText(Graphics g, String text) {
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        int centerX = this.getWidth()/2 - textWidth/2;
        return centerX;
    }
    
}
