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
    private Color shadowColor = Color.DARK_GRAY;
    private boolean shadow = false;

    public TitleScreen(Game game, GamePanel gamePanel, GameMouseListener mouseListener) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.mouseListener = mouseListener;
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
        drawButton(g, textStart, x, y, game::resume);

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
    }

    private void drawButton(Graphics g, String text, int x, int y, Runnable onClick) {
        Rectangle2D rect = getTextRectangle(g, text, x, y);
        if (isClicked(rect)) {
            backColor = clickBackColor;
            shadow = true;
            onClick.run();
        } else if (isHover(rect)) {
            backColor = hoverBackColor;
            shadow = false;
        } else {
            backColor = normalBackColor;
            shadow = false;
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
        int y = (int) rect.getY() - height/2;
        if (shadow) {
            g.setColor(shadowColor);
            g.fillRect(x+4, y+4, width, height);
        }
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

    private int getXForCenterText(Graphics g, String text) {
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        int centerX = gamePanel.getWidth()/2 - textWidth/2;
        return centerX;
    }
}
