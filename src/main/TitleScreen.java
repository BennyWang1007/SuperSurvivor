package main;

import listeners.GameMouseListener;
import utils.ImageTools;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TitleScreen {
    private enum MenuPage {
        MAIN,
        SELECT_MAP,
        SCOREBOARD,
        SETTING
    }

    private final Game game;
    private final GamePanel gamePanel;
    private final GameMouseListener mouseListener;
    private final String screenTitle = "超級倖存者";
    private final Color normalBackColor = Color.WHITE;
    private final Color hoverBackColor = Color.LIGHT_GRAY;
    private final Color clickBackColor = Color.GRAY;

    private MenuPage currentPage;

    private Stroke borderStroke = new BasicStroke(3);
    private Color textColor = Color.BLACK;
    private Color borderColor = Color.BLACK;
    private Color backColor = normalBackColor;
    private Map<String, Boolean> buttonClicked;

    private int selectedMapLevel = 1;
    private BufferedImage mapImage;
    private final int miniMapWidth = 400;
    private final int miniMapHeight = 400;

    // For start game animation
    private final int secondsToStartGame = 2;
    private long nanoTimeElapsed;
    private long previousNanoTime;
    private boolean startGame;

    public TitleScreen(Game game, GamePanel gamePanel, GameMouseListener mouseListener) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.mouseListener = mouseListener;
        init();
    }

    private void init() {
        currentPage = MenuPage.MAIN;
        selectedMapLevel = 1;
        nanoTimeElapsed = 0;
        previousNanoTime = 0;
        startGame = false;
        buttonClicked = new HashMap<>();
    }

    private void loadMapImage() {
        mapImage = ImageTools.scaleImage(gamePanel.gameMap.getMiniMap(), miniMapWidth, miniMapHeight);
    }

    public void draw(Graphics g) {
       if (currentPage == MenuPage.MAIN) {
           drawMainPage(g);
       } else if (currentPage == MenuPage.SELECT_MAP) {
           drawSelectMap(g);
       } else if (currentPage == MenuPage.SCOREBOARD) {
           drawScoreboard(g);
       } else if (currentPage == MenuPage.SETTING) {
           drawSettingsPage(g);
       }
    }

    private void drawMainPage(Graphics g) {
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
        y = gamePanel.getHeight()/2;
        drawButton(g, textStart, x, y, 75, true, true, () -> startGame = true);

        // CHOOSE LEVEL
        String textLevel = "選擇地圖";
        x = getXForCenterText(g, textLevel);
        y += textHeight + gap;
        drawButton(g, textLevel, x, y, 75, true, true, () -> {
            currentPage = MenuPage.SELECT_MAP;
            loadMapImage();
        });

        // SCOREBOARD
        String textScoreboard = "排行榜";
        x = getXForCenterText(g, textScoreboard);
        y += textHeight + gap;
        drawButton(g, textScoreboard, x, y, 75+getStringWidth(g, "中")/2, true, true, () -> {
            currentPage = MenuPage.SCOREBOARD;
        });

        // SETTINGS
        String textSetting = "設定";
        x = getXForCenterText(g, textSetting);
        y += textHeight + gap;
        drawButton(g, textSetting, x, y, 75+getStringWidth(g, "中中")/2, true, true, () -> {
            currentPage = MenuPage.SETTING;
        });

        // QUIT GAME
        String textQuit = "離開遊戲";
        x = getXForCenterText(g, textQuit);
        y += textHeight + gap;
        drawButton(g, textQuit, x, y, 75, true, true, game::quit);


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

    private void drawSelectMap(Graphics g) {
        int x, y;
        Rectangle2D rect;

        // map display frame
        x = gamePanel.getWidth()/2 - miniMapWidth /2;
        y = gamePanel.getHeight()/3 - miniMapHeight /2;
        g.drawImage(mapImage, x, y, miniMapWidth, miniMapHeight, null);

        // map display border
        x = x - 1;
        y = y - 1;
        g.setColor(Color.BLACK);
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, miniMapWidth, miniMapHeight, 3, 3);

        // map description
        String mapDescription = "地圖" + selectedMapLevel;
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = getXForCenterText(g, mapDescription);
        y = (y + miniMapHeight) + 75;
        rect = getTextRectangle(g, mapDescription, x, y, 0);
        g.drawString(mapDescription, x, y);

        // left select button
        String leftButton = "《";
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = (int) (rect.getX() - 100);
        drawButton(g, leftButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 - 1 + gamePanel.mapNums) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // right select button
        String rightButton = "》";
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = (int) (rect.getX() + rect.getWidth() + 100 - getStringWidth(g, rightButton));
        drawButton(g, rightButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 + 1) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // save & back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "儲存並返回";
        x = getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawScoreboard(Graphics g) {
        int x, y;

        // TITLE
        String title = "排行榜";
        g.setFont(g.getFont().deriveFont(48f));
        g.setColor(Color.BLACK);
        x = getXForCenterText(g, title);
        y = gamePanel.getHeight()/8;
        g.drawString(title, x, y);

        // Rank list
        // TODO: Rank List

        // back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawSettingsPage(Graphics g) {
        int x, y;

        // TITLE
        String title = "設定";
        g.setFont(g.getFont().deriveFont(48f));
        g.setColor(Color.BLACK);
        x = getXForCenterText(g, title);
        y = gamePanel.getHeight()/8;
        g.drawString(title, x, y);

        // back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawButton(Graphics g, String text, int x, int y, int horzPadding, boolean animation, boolean border, Runnable onClick) {
        Rectangle2D rect = getTextRectangle(g, text, x, y, horzPadding);
        boolean clicked = buttonClicked.getOrDefault(text, false);
        if (!startGame) {
            if (isClicked(rect)) {
                if (animation) backColor = clickBackColor;
                if (!clicked) buttonClicked.put(text, true);
            } else {
                if (isHover(rect)) {
                    if (animation) backColor = hoverBackColor;
                } else {
                    backColor = normalBackColor;
                }
                if (clicked) {
                    buttonClicked.put(text, false);
                    onClick.run();
                }
            }
        } else {
            backColor = normalBackColor;
        }
        if (border) drawTextBounds(g, rect);
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

    private Rectangle2D getTextRectangle(Graphics g, String text, int x, int y, int horzPadding) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
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
        int textWidth = getStringWidth(g, text);
        int centerX = gamePanel.getWidth()/2 - textWidth/2;
        return centerX;
    }

    private int getStringWidth(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }
}
