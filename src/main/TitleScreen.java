package main;

import utils.ImageTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    private int scrollY = 0;
    private static final int SCROLL_SPEED = 20;
    private BufferedImage scoreboardImage;

    private int scoreImageWidth = 0;
    private int scoreImageHeight = 0;
    // For start game animation
    private final int secondsToStartGame = 2;
    private long nanoTimeElapsed;
    private long previousNanoTime;

    public TitleScreen(Game game, GamePanel gamePanel) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.gamePanel.addMouseWheelListener(
            e -> handleMouseWheelEvent(e)
        );
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

    public void openSetting() {
        game.gameState = GameState.TITLE_SCREEN;
        currentPage = MenuPage.SETTING;
    }

    private void loadMapImage() {
        mapImage = ImageTools.scaleImage(gamePanel.gameMap.getMiniMap(), miniMapWidth, miniMapHeight);
    }

    public void draw(Graphics g) {
        // draw background
        g.drawImage(titleScreenImage, 0, 0, game.screenWidth, game.screenHeight,null );
        g.setColor(new Color(0xFF, 0xFF, 0xFF, 128));
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        if (currentPage != MenuPage.SCOREBOARD) {
            g.drawImage(playerImage,800,500,72,96,null);
        }

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
        gamePanel.drawButton(g, "start", textStart, x, y, 75, true, true, () -> gamePanel.startGame = true);

        // CHOOSE LEVEL
        String textLevel = "選擇地圖";
        x = gamePanel.getXForCenterText(g, textLevel);
        y += textHeight + gap;
        gamePanel.drawButton(g, "choose_map", textLevel, x, y, 75, true, true, () -> {
            currentPage = MenuPage.SELECT_MAP;
            loadMapImage();
        });

        // SCOREBOARD
        String textScoreboard = "排行榜";
        x = gamePanel.getXForCenterText(g, textScoreboard);
        y += textHeight + gap;
        gamePanel.drawButton(g, "scoreboard", textScoreboard, x, y, 75+gamePanel.getStringWidth(g, "中")/2, true, true, () -> {
            currentPage = MenuPage.SCOREBOARD;
        });

        // SETTINGS
        String textSetting = "設定";
        x = gamePanel.getXForCenterText(g, textSetting);
        y += textHeight + gap;
        gamePanel.drawButton(g, "menu_setting", textSetting, x, y, 75+gamePanel.getStringWidth(g, "中中")/2, true, true, () -> {
            currentPage = MenuPage.SETTING;
        });

        // QUIT GAME
        String textQuit = "離開遊戲";
        x = gamePanel.getXForCenterText(g, textQuit);
        y += textHeight + gap;
        gamePanel.drawButton(g, "exit", textQuit, x, y, 75, true, true, game::quit);


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
        gamePanel.drawButton(g, "map_left", leftButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 - 1 + gamePanel.mapNums) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // right select button
        String rightButton = "》";
        g.setFont(g.getFont().deriveFont(36f));
        g.setColor(Color.BLACK);
        x = (int) (rect.getX() + rect.getWidth() + 100 - gamePanel.getStringWidth(g, rightButton));
        gamePanel.drawButton(g, "map_right", rightButton, x, y, 0, false, false, () -> {
            selectedMapLevel = (selectedMapLevel - 1 + 1) % gamePanel.mapNums + 1;
            gamePanel.setMap(selectedMapLevel);
            loadMapImage();
        });

        // save & back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "儲存並返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight()*7/8;
        gamePanel.drawButton(g, "map_save", backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    private void drawScoreboard(Graphics g) {
        int x, y;
        int width, height;
    
        // TITLE
        String title = "排行榜";
        g.setFont(g.getFont().deriveFont(48f));
        x = gamePanel.getXForCenterText(g, title);
        y = gamePanel.getHeight() / 8;
        g.setColor(Color.GRAY);
        g.drawString(title, x + 3, y + 3);
        g.setColor(Color.BLACK);
        g.drawString(title, x, y);
    
        // Background
        x = gamePanel.getWidth() / 7;
        y = gamePanel.getHeight() / 6;
        width = gamePanel.getWidth() - x * 2;
        height = gamePanel.getHeight() * 4 / 6;
        g.setColor(new Color(102, 102, 102, 160));
        g.fillRoundRect(x, y, width, height, 20, 20);

        // draw 2 buttons for scrolling at bottom right
        int buttonHeight = 50;
        int buttonGap = 20;
        int buttonX = x + width + 20;
        int buttonY = y + height - buttonHeight - buttonGap - 10;
        gamePanel.drawButton(g, "scoreboard_up", "↑", buttonX, buttonY, 10, true, true, () -> scoreScroll(-SCROLL_SPEED));
        gamePanel.drawButton(g, "scoreboard_down", "↓", buttonX, buttonY + buttonHeight + buttonGap, 10, true, true, () -> scoreScroll(SCROLL_SPEED));

        // Generate the scoreboard image if it doesn't exist or needs to be updated
        scoreImageWidth = width - 30;
        scoreImageHeight = height - 30;
        if (scoreboardImage == null) {
            int textHeight = (int) g.getFontMetrics().getStringBounds("中", g).getHeight();
            int offScreenHeight = Game.scores.size() * (textHeight + 10) + 20; // calculate based on the number of scores
            scoreboardImage = new BufferedImage(scoreImageWidth, offScreenHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics gOffScreen = scoreboardImage.getGraphics();
    
            // Draw the rank list
            int cx = 15;
            int cy = 30;
            gOffScreen.setFont(g.getFont().deriveFont(36f));
            for (int i = 0; i < Game.scores.size(); i++) {
                ScoreEntry entry = Game.scores.get(i);
                String text = (i + 1) + ". " + entry.getName() + " - " + entry.getScore() + "分";
                gOffScreen.setFont(g.getFont().deriveFont(36f));
                gOffScreen.setColor(Color.BLACK);
                gOffScreen.drawString(text, cx + 3, cy + 3);
                gOffScreen.setColor(new Color(220, 220, 220));
                gOffScreen.drawString(text, cx, cy);
                
                // draw timestamp with smaller font at the right
                gOffScreen.setFont(g.getFont().deriveFont(24f));
                LocalDateTime timestamp = entry.getTimestamp();
                
                String timeStr = String.format("%04d/%02d/%02d %02d:%02d",
                    timestamp.getYear(),
                    timestamp.getMonthValue(),
                    timestamp.getDayOfMonth(),
                    timestamp.getHour(),
                    timestamp.getMinute()
                );
                gOffScreen.setColor(Color.BLACK);
                gOffScreen.drawString(timeStr, scoreImageWidth - (g.getFontMetrics().stringWidth(timeStr) / 2) - 15, cy);

                cy += textHeight;
            }
    
            gOffScreen.dispose();
        }
    
        // Draw the visible portion of the scoreboard image
        int clipX = x + 15;
        int clipY = y + 15;
        g.drawImage(scoreboardImage, clipX, clipY, clipX + scoreImageWidth, clipY + scoreImageHeight,
                    0, scrollY, scoreImageWidth, scrollY + scoreImageHeight, null);
    
        // Draw the back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight() * 8 / 9;
        gamePanel.drawButton(g, "scoreboard_back", backString, x, y, 75, true, true, () -> currentPage = MenuPage.MAIN);
    }

    public void scoreScroll(int dy) {
        if (dy > 0) {
            scrollY = Math.min(scrollY + dy, Math.max(0, scoreboardImage.getHeight() - scoreImageHeight));
        } else {
            scrollY = Math.max(0, scrollY + dy);
        }
    }

    public void handleMouseWheelEvent(MouseWheelEvent e) {
        if (currentPage == MenuPage.SCOREBOARD) {
            int notches = e.getWheelRotation();
            scoreScroll(notches * SCROLL_SPEED);
        }
    }

    private void drawSettingsPage(Graphics g) {
        int x, y;
        int width, height;

        // TITLE
        String title = "設定";
        g.setFont(g.getFont().deriveFont(48f));
        x = gamePanel.getXForCenterText(g, title);
        y = gamePanel.getHeight()/8;
        g.setColor(Color.GRAY);
        g.drawString(title, x+3, y+3);
        g.setColor(Color.BLACK);
        g.drawString(title, x, y);

        // background
        x = gamePanel.getWidth()*2/6;
        y = gamePanel.getHeight()/6;
        width = gamePanel.getWidth()*2/6;
        height = gamePanel.getHeight()*4/6;
        g.setColor(new Color(102, 102, 102, 250));
        g.fillRoundRect(x, y, width, height, 20, 20);

        g.setFont(g.getFont().deriveFont(28f));
        int padding = 20;
        int labelX = x + padding;
        int buttonX = x + width/2;
        int textHeight = gamePanel.getStringHeight(g, "中");
        // player name
        String labelPlayerName = "玩家名稱";
        String playerName = game.player.getName();
        boolean cut = false;
        while (gamePanel.getStringWidth(g, playerName) > width*3/8) {
            playerName = playerName.substring(0, playerName.length()-1);
            cut = true;
        }
        if (cut) playerName += "...";
        y += textHeight + padding;
        g.setColor(Color.DARK_GRAY);
        g.drawString(labelPlayerName, labelX+3, y+3);
        g.setColor(Color.WHITE);
        g.drawString(labelPlayerName, labelX, y);
        gamePanel.drawButton(g, "menu_setting_playerName", playerName, buttonX, y, 10, true, true, () -> {
            String input = JOptionPane.showInputDialog(game.gameFrame, "原始名稱：" + game.player.getName() + "\n輸入玩家名稱");
            if (input == null) return;
            input = input.trim();
            if (input.isEmpty()) return;
            game.player.setName(input);
        });

        // music voice
        String labelMusic = "背景音樂";
        y += textHeight + padding;
        g.setColor(Color.DARK_GRAY);
        g.drawString(labelMusic, labelX+3, y+3);
        g.setColor(Color.WHITE);
        g.drawString(labelMusic, labelX, y);
        // minus button
        gamePanel.drawButton(g, "menu_setting_music_minus", "-", buttonX, y, 10, true, true, () -> {
            int newLevel = game.settings.musicVolumeLevel - 1;
            if (newLevel < 0) newLevel = 0;
            game.setBgmVolume(newLevel);
            game.settings.musicVolumeLevel = newLevel;
        });
        // music volume
        gamePanel.drawButton(g, "None", ""+game.settings.musicVolumeLevel, buttonX+50, y, 10, true, true, () -> {});
        // plus button
        gamePanel.drawButton(g, "menu_setting_music_plus", "+", buttonX+100, y, 10, true, true, () -> {
            int newLevel = game.settings.musicVolumeLevel + 1;
            if (newLevel > 5) newLevel = 5;
            game.setBgmVolume(newLevel);
            game.settings.musicVolumeLevel = newLevel;
        });

        // sound effect
        String labelSound = "遊戲音效";
        y += textHeight + padding;
        g.setColor(Color.DARK_GRAY);
        g.drawString(labelSound, labelX+3, y+3);
        g.setColor(Color.WHITE);
        g.drawString(labelSound, labelX, y);
        // minus button
        gamePanel.drawButton(g, "menu_setting_sound_minus", "-", buttonX, y, 10, true, true, () -> {
            int newLevel = game.settings.soundVolumeLevel - 1;
            if (newLevel < 0) newLevel = 0;
            game.setSoundVolume(newLevel);
            game.settings.soundVolumeLevel = newLevel;
        });
        // music volume
        gamePanel.drawButton(g, "None", ""+game.settings.soundVolumeLevel, buttonX+50, y, 10, true, true, () -> {});
        // plus button
        gamePanel.drawButton(g, "menu_setting_sound_plus", "+", buttonX+100, y, 10, true, true, () -> {
            int newLevel = game.settings.soundVolumeLevel + 1;
            if (newLevel > 5) newLevel = 5;
            game.setSoundVolume(newLevel);
            game.settings.soundVolumeLevel = newLevel;
        });

        // back button
        g.setFont(g.getFont().deriveFont(36f));
        String backString = "返回";
        x = gamePanel.getXForCenterText(g, backString);
        y = gamePanel.getHeight()*8/9;
        gamePanel.drawButton(g, "menu_setting_back", backString, x, y, 75, true, true, () -> {
            if (game.isInGame()) {
                game.pause();
            } else {
                currentPage = MenuPage.MAIN;
            }
        });
    }

    private void drawStartGameAnimation(Graphics g) {
        double progressRatio = (nanoTimeElapsed / 1000000000.) / secondsToStartGame;
        int x = 0, y = 0;
        g.setColor(new Color(0x87, 0x87, 0x87, (int)(255*progressRatio)));
        g.fillRect(x, y, gamePanel.getWidth(), gamePanel.getHeight());
    }

}
