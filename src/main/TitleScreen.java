package main;

import listeners.GameMouseListener;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TitleScreen {
    private final Game game;
    private final GamePanel gamePanel;
    private final GameMouseListener mouseListener;
    private final String screenTitle = "超級倖存者";
    private final Color normalBackColor = Color.WHITE;
    private final Color hoverBackColor = Color.LIGHT_GRAY;
    private final Color clickBackColor = Color.GRAY;

    private Stroke borderStroke = new BasicStroke(3);
    private Color textColor = Color.BLACK;
    private Color borderColor = Color.BLACK;
    private Color backColor = normalBackColor;

    // For start game animation
    private final int secondsToStartGame = 2;
    private long nanoTimeElapsed;
    private long previousNanoTime;
    private boolean startGame;

    public TitleScreen(Game game, GamePanel gamePanel, GameMouseListener mouseListener) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.mouseListener = mouseListener;
        nanoTimeElapsed = 0;
        previousNanoTime = 0;
        startGame = false;
    }

    public void draw(Graphics g) {
        g.setFont(g.getFont().deriveFont(96f));

        // MAIN TITLE SHADOW
        int x = getXForCenterText(g, screenTitle);
        int y = gamePanel.getHeight()/4;
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
        y = gamePanel.getHeight()*4/7;
        drawButton(g, textStart, x, y, () -> startGame = true);

        // CHOOSE LEVEL
        String textLevel = "選擇關卡";
        x = getXForCenterText(g, textLevel);
        y += textHeight + gap;
        drawButton(g, textLevel, x, y, () -> {});

        // QUIT GAME
        String textQuit = "離開遊戲";
        x = getXForCenterText(g, textQuit);
        y += textHeight + gap;
        drawButton(g, textQuit, x, y, game::quit);

        if (startGame) {
            drawStartGameAnimation(g);

            if (previousNanoTime == 0) previousNanoTime = System.nanoTime();
            long currentNanoTime = System.nanoTime();
            nanoTimeElapsed += (currentNanoTime - previousNanoTime);
            previousNanoTime = currentNanoTime;
            if (nanoTimeElapsed >= secondsToStartGame*1000000000L) {
                game.resume();
                startGame = false;
            }
        }
    }

    private void drawButton(Graphics g, String text, int x, int y, Runnable onClick) {
        Rectangle2D rect = getTextRectangle(g, text, x, y);
        if (!startGame) {
            if (isClicked(rect)) {
                backColor = clickBackColor;
                onClick.run();
            } else if (isHover(rect)) {
                backColor = hoverBackColor;
            } else {
                backColor = normalBackColor;
            }
        } else {
            backColor = normalBackColor;
        }
        drawTextBounds(g, rect);
        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    private void drawTextBounds(Graphics g, Rectangle2D rect) {
        Stroke originalStroke = ((Graphics2D)g).getStroke();
        int width = (int) rect.getWidth();
        int height = (int) rect.getHeight();
        int x = (int) rect.getX();
        int y = (int) rect.getY();
        g.setColor(backColor);
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
        y = (int) (y - rect.getHeight()*73/100.);
        rect.setRect(x, y, width, height);
        return rect;
    }

    private void drawStartGameAnimation(Graphics g) {
        double progressRatio = (nanoTimeElapsed / 1000000000.) / secondsToStartGame;
        int x = 0, y = 0;
        g.setColor(new Color(0x87, 0x87, 0x87, (int)(255*progressRatio)));
        g.fillRect(x, y, gamePanel.getWidth(), gamePanel.getHeight());
    }

    private boolean isHover(Rectangle2D rect) {
        return rect.contains(mouseListener.mouseX, mouseListener.mouseY);
    }

    private boolean isClicked(Rectangle2D rect) {
        return mouseListener.mouseClicked && isHover(rect);
    }

    private int getXForCenterText(Graphics g, String text) {
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        int centerX = gamePanel.getWidth()/2 - textWidth/2;
        return centerX;
    }
}
