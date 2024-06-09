package main;

import utils.ImageTools;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class TitleScreen {
    private enum MenuPage {
        MAIN,
        SELECT_MAP,
        SCOREBOARD,
        SETTING
    }

    private final Game game;
    private final GamePanel gamePanel;
    private final String screenTitle = "超級倖存者";

    private MenuPage currentPage;

    private int selectedMapLevel = 1;
    private BufferedImage mapImage;
    private final int miniMapWidth = 400;
    private final int miniMapHeight = 400;

    private BufferedImage playerImage;
    private BufferedImage titleScreenImage;

    // For start game animation
    private final int secondsToStartGame = 2;
    private long nanoTimeElapsed;
    private long previousNanoTime;

    public TitleScreen(Game game, GamePanel gamePanel) {
        this.game = game;
        this.gamePanel = gamePanel;
        titleScreenImage = ImageTools.scaleImage(ImageTools.readImage("/ui/titlescreen.png"),863,483);
        playerImage = ImageTools.scaleImage(ImageTools.readImage("/player/backward1.png"), 36, 48);
        init();
    }

    public void init() {
        currentPage = MenuPage.MAIN;
        selectedMapLevel = 1;
        nanoTimeElapsed = 0;
        previousNanoTime = 0;
    }

    private void loadMapImage() {
        mapImage = ImageTools.scaleImage(gamePanel.gameMap.getMiniMap(), miniMapWidth, miniMapHeight);
    }

    public void draw(Graphics g) {
        // draw background
        g.drawImage(titleScreenImage, 0, 0, game.screenWidth, game.screenHeight,null );
        g.setColor(new Color(0xFF, 0xFF, 0xFF, 128));
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        g.drawImage(playerImage,game.screenCenterX+800,game.screenCenterY+500,72,96,null);

        // draw body
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
        int x = gamePanel.getXForCenterText(g, screenTitle);
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
        x = gamePanel.getXForCenterText(g, textStart);
        y = gamePanel.getHeight()/2;
        gamePanel.drawButton(g, textStart, x, y, 75, true, true, () -> gamePanel.startGame = true);

        // CHOOSE LEVEL
        String textLevel = "選擇地圖";
        x = gamePanel.getXForCenterText(g, textLevel);
        y += textHeight + gap;
        gamePanel.drawButton(g, textLevel, x, y, 75, true, true, () -> {
            currentPage = MenuPage.SELECT_MAP;
            loadMapImage();
        });

        // SCOREBOARD
        String textScoreboard = "排行榜";
        x = gamePanel.getXForCenterText(g, textScoreboard);
        y += textHeight + gap;
        gamePanel.drawButton(g, textScoreboard, x, y, 75+gamePanel.getStringWidth(g, "中")/2, true, true, () -> {
            currentPage = MenuPage.SCOREBOARD;
        });

        // SETTINGS
        String textSetting = "設定";
        x = gamePanel.getXForCenterText(g, textSetting);
        y += textHeight + gap;
        gamePanel.drawButton(g, textSetting, x, y, 75+gamePanel.getStringWidth(g, "中中")/2, true, true, () -> {
            currentPage = MenuPage.SETTING;
        });

        // QUIT GAME
        String textQuit = "離開遊戲";
        x = gamePanel.getXForCenterText(g, textQuit);
        y += textHeight + gap;
        gamePanel.drawButton(g, textQuit, x, y, 75, true, true, game::quit);


        if (gamePanel.startGame) {
            drawStartGameAnimation(g);

            if (previousNanoTime == 0) previousNanoTime = System.nanoTime();
            long currentNanoTime = System.nanoTime();
            nanoTimeElapsed += (currentNanoTime - previousNanoTime);
            previousNanoTime = currentNanoTime;
            if (nanoTimeElapsed >= secondsToStartGame*1000000000L) {
                game.resume();
                gamePanel.startGame = false;
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
        x = gamePanel.getXForCenterText(g, mapDescription);
        y = (y + miniMapHeight) + 75;
        rect = gamePanel.getTextRectangle(g, mapDescription, x, y, 0);
        g.drawString(mapDescription, x, y);

        // left select button
        String leftButton = "《";
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = (int) (rect.getX() - 100);
        gamePanel.drawButton(g, leftButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 - 1 + gamePanel.mapNums) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // right select button
        String rightButton = "》";
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = (int) (rect.getX() + rect.getWidth() + 100 - gamePanel.getStringWidth(g, rightButton));
        gamePanel.drawButton(g, rightButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 + 1) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // save & back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "儲存並返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        gamePanel.drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawScoreboard(Graphics g) {
        int x, y;

        // TITLE
        String title = "排行榜";
        g.setFont(g.getFont().deriveFont(48f));
        g.setColor(Color.BLACK);
        x = gamePanel.getXForCenterText(g, title);
        y = gamePanel.getHeight()/8;
        g.drawString(title, x, y);

        // Rank list
        // TODO: Rank List

        // back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        gamePanel.drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawSettingsPage(Graphics g) {
        int x, y;

        // TITLE
        String title = "設定";
        g.setFont(g.getFont().deriveFont(48f));
        g.setColor(Color.BLACK);
        x = gamePanel.getXForCenterText(g, title);
        y = gamePanel.getHeight()/8;
        g.drawString(title, x, y);

        // back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        gamePanel.drawButton(g, backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawStartGameAnimation(Graphics g) {
        double progressRatio = (nanoTimeElapsed / 1000000000.) / secondsToStartGame;
        int x = 0, y = 0;
        g.setColor(new Color(0x87, 0x87, 0x87, (int)(255*progressRatio)));
        g.fillRect(x, y, gamePanel.getWidth(), gamePanel.getHeight());
    }

}
