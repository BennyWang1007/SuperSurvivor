package main;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import entity.*;
import entity.monster.Monster;
import listeners.GameMouseListener;
import weapons.*;
import utils.ImageTools;

public class GamePanel extends Canvas {

    private final Game game;
    private Player player;
    private Set<Monster> monsters;
    private Set<DropItem> dropItems;
    private Set<Projectile> projectiles;

    private ArrayList<LevelUpChoice> levelUpChoices;
    private LevelUpChoice[] curLevelUpChoices = new LevelUpChoice[3];

    public final GameMap gameMap;
    public final int mapNums = 3;

    // Title screen
    private GameMouseListener mouseListener;
    private final TitleScreen titleScreen;
    Font cubicFont;
    final Color normalBackColor = Color.WHITE;
    final Color hoverBackColor = Color.LIGHT_GRAY;
    final Color clickBackColor = Color.GRAY;
    Stroke borderStroke = new BasicStroke(3);
    Color textColor = Color.BLACK;
    Color borderColor = Color.BLACK;
    Color backColor = normalBackColor;
    Map<String, Boolean> buttonClicked;
    boolean startGame;
    
    private static final boolean DEBUG = true;

    public GamePanel(Game game, GameMouseListener mouseListener) {
        super();
        this.game = game;
        this.mouseListener = mouseListener;
        this.gameMap = new GameMap(game, this);
        this.titleScreen = new TitleScreen(game, this);
        setupFont();
        setupScreenSize(game.screenWidth, game.screenHeight);
        buttonClicked = new HashMap<>();
    }

    public void init() {
        initLevelUpChoices();
        titleScreen.init();
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

    public void setMap(int level) {
        gameMap.loadMap("/maps/lv" + level + ".txt");
    }

    public void initLevelUpChoices() {
        levelUpChoices = new ArrayList<>();
        levelUpChoices.add(new LevelUpChoice("Spinning Sword", LevelUpChoice.ADD_WEAPON, new SpinningSword(game, 100, 100, player.attack, 300, 100, player), player));
        levelUpChoices.add(new LevelUpChoice("Aura", LevelUpChoice.ADD_WEAPON, new Aura(game, 150, 150, 0.5f, 75, player), player));
        levelUpChoices.add(new LevelUpChoice("Bow Lv.2", LevelUpChoice.UPGRADE_WEAPON, player.getBow(), player));

        levelUpChoices.add(new LevelUpChoice("Atk + 20", LevelUpChoice.UPGRADE_PLAYER, LevelUpChoice.UPGRADE_ATK, 20, player));
        levelUpChoices.add(new LevelUpChoice("Def + 10", LevelUpChoice.UPGRADE_PLAYER, LevelUpChoice.UPGRADE_DEF, 10, player));
        levelUpChoices.add(new LevelUpChoice("Hp + 50", LevelUpChoice.UPGRADE_PLAYER, LevelUpChoice.UPGRADE_HP, 50, player));
        levelUpChoices.add(new LevelUpChoice("Speed + 5", LevelUpChoice.UPGRADE_PLAYER, LevelUpChoice.UPGRADE_SPD, 5, player));

        randomCurLevelUpChoices();
    }

    // randomly choose 3 choices from levelUpChoices to curLevelUpChoices
    private void randomCurLevelUpChoices() {
        // System.out.println("Random curLevelUpChoices from size " + levelUpChoices.size());
        Collections.shuffle(levelUpChoices);
        for (int i = 0; i < 3; i++) {
            curLevelUpChoices[i] = levelUpChoices.get(i);
        }
    }

    public void setPlayer(Player player) { this.player = player; }
    public void setMonsters(Set<Monster> monsters) { this.monsters = monsters; }
    public void setDropItems(Set<DropItem> dropItems) { this.dropItems = dropItems; }
    public void setProjectiles(Set<Projectile> projectiles) { this.projectiles = projectiles; }

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
        } else if (gameState != GameState.TITLE_SCREEN) {
            drawBackground(g);
            // draw : monster -> weapon -> player
            drawMonsters(g);
            drawExp(g);
            drawProjectiles(g);
            drawPlayer(g);
            drawDamageReceived(g);
            if (DEBUG) {
                // draw a rectangle as background of debug info
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 310, 100 + 20 * player.getWeapons().size());
                // info
                g.setColor(Color.WHITE);
                g.setFont(getFont().deriveFont(20.0f));
                g.drawString("Player: (" + (int)player.x + ", " + (int)player.y + "), atk: " + player.attack, 10, 35);
                g.drawString("def: " + player.defense + ", hp: " + player.hp + " / " + player.maxHp + ", spd: " + player.speed, 10, 55);
                g.drawString("Exp: " + player.exp + "/" + player.expTable[player.level] + ", Level: " + player.level, 10, 75);
                if (player.getWeapons().size() > 0) {
                    g.drawString("Weapons: ", 10, 95);
                    int i = 0;
                    for (Weapon weapon : player.getWeapons()) {
                        g.drawString(weapon.getClass().getSimpleName() + " lv: " + weapon.getLevel() + "atk " + weapon.attack, 10, 115 + 20 * i);
                        i++;
                    }
                }
                g.setFont(getFont().deriveFont(12.0f));
            }
            drawFPS(g);
            g.setFont(cubicFont);

            if (gameState == GameState.PAUSE) {
                drawPauseView(g);
            }
            if (gameState == GameState.LEVEL_UP) {
                drawUpdateScreen(g);
            }
        }
        g.dispose();
        bs.show();
    }

    private void drawBackground(Graphics g) {
        gameMap.draw(g);
    }

    private void drawPauseView(Graphics g) {
        int x, y;

        // background
        g.setColor(new Color(0xFF, 0xFF, 0xFF, 128));
        g.fillRect(0, 0, getWidth(), getHeight());

        // sub background
        x = getWidth()*2/7;
        y = getHeight()/7;
        int width = getWidth()*3/7;
        int height = getHeight()*5/7;
        g.setColor(new Color(102, 102, 102, 200));
        g.fillRoundRect(x, y, width, height, 5, 5);

        // title
        g.setFont(g.getFont().deriveFont(64f));
        String textPause = "暫停";
        x = getXForCenterText(g, textPause);
        y = getHeight()/4;
        g.setColor(new Color(143, 0, 0));
        g.drawString(textPause, x+3, y+3);
        g.setColor(Color.RED);
        g.drawString(textPause, x, y);

        // button setup
        g.setFont(g.getFont().deriveFont(48f));
        g.setColor(Color.BLACK);
        int buttonGap = 30;
        int textHeight = (int) g.getFontMetrics().getStringBounds("中", g).getHeight();
        // resume button
        String textResume = "繼續遊戲";
        x = getXForCenterText(g, textResume);
        y = getHeight()/2;
        drawButton(g, textResume, x, y, 25, true, true, game::resume);

        // setting button
        String textSetting = "設定";
        x = getXForCenterText(g, textSetting);
        y += textHeight + buttonGap;
        drawButton(g, textSetting, x, y, 25+getStringWidth(g, "中中")/2, true, true, () -> {});

        // back to main menu button
        String textMainMenu = "回到主頁";
        x = getXForCenterText(g, textMainMenu);
        y += textHeight + buttonGap;
        drawButton(g, textMainMenu, x, y, 25, true, true, game::reset);

    }

    private void drawUpdateScreen(Graphics g) {

        // draw a white rectangle as background with opacity 0.5
        g.setColor(new Color(255, 255, 255, 128));
        g.fillRect(0, 0, getWidth(), getHeight());

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int boxWidth = 200;
        int boxHeight = 200;
        int margin = 20;

        int boxX[] = {panelWidth / 2 - boxWidth / 2 * 3 - margin, panelWidth / 2 - boxWidth / 2, panelWidth / 2 + boxWidth / 2 + margin};
        int boxY = panelHeight / 2 - boxHeight / 2;

        g.setColor(Color.BLACK);
        ((Graphics2D) g).setStroke(new BasicStroke(3));

        g.setFont(getFont().deriveFont(20.0f));
        g.drawString("Choose one upgrade", panelWidth / 2 - "Choose one upgrade".length() * 5, boxY - 20);
        for (int i = 0; i < 3; i++) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(boxX[i], boxY, boxWidth, boxHeight, 10, 10);
            g.drawImage(curLevelUpChoices[i].iconImage, boxX[i] + 25, boxY + 10, boxWidth - 50, boxHeight - 50, null);
            g.setColor(Color.BLACK);
            g.drawRoundRect(boxX[i], boxY, boxWidth, boxHeight, 10, 10);
            g.drawString(curLevelUpChoices[i].getName(), boxX[i] + boxWidth / 2 - curLevelUpChoices[i].getName().length() * 5, boxY + boxHeight - 20);
        }

        int smallBoxWidth = 100;
        int smallBoxHeight = 100;

        int smallBoxX[] = {panelWidth / 2 - smallBoxWidth * 3 - margin / 2 * 5, panelWidth / 2 - smallBoxWidth * 2 - margin / 2 * 3, panelWidth / 2 - smallBoxWidth - margin / 2, panelWidth / 2 + margin / 2, panelWidth / 2 + smallBoxWidth + margin / 2 * 3, panelWidth / 2 + smallBoxWidth * 2 + margin / 2 * 5};
        int smallBoxY = panelHeight / 2 + boxHeight / 2 + margin;


        for (int i = 0; i < 6; i++) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(smallBoxX[i], smallBoxY, smallBoxWidth, smallBoxHeight, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(smallBoxX[i], smallBoxY, smallBoxWidth, smallBoxHeight, 10, 10);
        }
        int weaponIdx = 0;
        for (Weapon weapon : player.getWeapons()) {
            // fill the IconImage to the small box
            g.drawImage(weapon.iconImage, smallBoxX[weaponIdx] + 5, smallBoxY + 5, smallBoxWidth - 10, smallBoxHeight - 10, null);

            String str = " Lv." + weapon.getLevel();
            g.drawString(str, smallBoxX[weaponIdx] + smallBoxWidth / 2 - str.length() * 5, smallBoxY + smallBoxHeight + 20);
            weaponIdx++;
        }
        
        if (mouseListener.mouseClicked) {
            for (int i = 0; i < 3; i++) {
                if (mouseListener.mouseX > boxX[i] && mouseListener.mouseX < boxX[i] + boxWidth &&
                    mouseListener.mouseY > boxY && mouseListener.mouseY < panelHeight / 2 + boxHeight / 2) {
                    curLevelUpChoices[i].apply();
                    LevelUpChoice nextChoice = curLevelUpChoices[i].nextUpgrade();
                    if (nextChoice == null) {
                        levelUpChoices.remove(curLevelUpChoices[i]);
                    } else {
                        levelUpChoices.set(levelUpChoices.indexOf(curLevelUpChoices[i]), nextChoice);
                    }
                    randomCurLevelUpChoices();
                    game.resume();
                }
            }
        }
    }

    private void drawMonsters(Graphics g) {
        monsters.forEach(monster -> monster.draw(g));
    }

    private void drawProjectiles(Graphics g) {
        projectiles.forEach(projectile -> projectile.draw(g));
    }

    private void drawExp(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(getFont().deriveFont(20.0f));
        g.drawString("Level: " + player.level, 10, 75);
        int x = 350, y = 100, h = 50, arc = 10;
        int w = getWidth() - 2 * x;
        // draw the background of the exp bar
        g.setColor(Color.GREEN);
        int expWidth = Math.min((int) (w * (float) player.exp / player.expTable[player.level]), w);
        g.fillRoundRect(x, y, expWidth, h, arc, arc);
        // draw the edge of the exp bar
        g.setColor(Color.BLUE);
        ((Graphics2D) g).setStroke(new BasicStroke(3));

        g.drawRoundRect(x, y, w, h, arc, arc);
        dropItems.forEach(item -> item.draw(g));
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

    int getXForCenterText(Graphics g, String text) {
        int textWidth = getStringWidth(g, text);
        int centerX = getWidth()/2 - textWidth/2;
        return centerX;
    }

    int getStringWidth(Graphics g, String text) {
        return (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
    }

    void drawButton(Graphics g, String text, int x, int y, int horzPadding, boolean animation, boolean border, Runnable onClick) {
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

    void drawTextBounds(Graphics g, Rectangle2D rect) {
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

    Rectangle2D getTextRectangle(Graphics g, String text, int x, int y, int horzPadding) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        int width = (int) rect.getWidth() + 2*horzPadding;
        int height = (int) rect.getHeight();
        x = x - horzPadding;
        y = (int) (y - rect.getHeight()*73/100.);
        rect.setRect(x, y, width, height);
        return rect;
    }

    boolean isHover(Rectangle2D rect) {
        return rect.contains(mouseListener.mouseX, mouseListener.mouseY);
    }

    boolean isClicked(Rectangle2D rect) {
        return mouseListener.mouseClicked && isHover(rect);
    }
    
}

class LevelUpChoice {
    private String name;
    private final Player player;
    public final int type; // 1: Add weapon, 2: Upgrade weapon, 3. Upgrade player ability
    private Weapon weapon;
    private int abilityType;
    private int abilityValue;
    public BufferedImage iconImage;

    public static final int ADD_WEAPON = 1;
    public static final int UPGRADE_WEAPON = 2;
    public static final int UPGRADE_PLAYER = 3;

    public static final int UPGRADE_ATK = 1;
    public static final int UPGRADE_DEF = 2;
    public static final int UPGRADE_HP = 3;
    public static final int UPGRADE_SPD = 4;

    public static final BufferedImage[] weaponImages = loadWeaponImages();
    public static final BufferedImage[] abilityImages = loadAbilityImages();

    public LevelUpChoice(String name, int type, Weapon weapon, Player player) {
        this.name = name;
        this.type = type;
        this.weapon = weapon;
        this.player = player;
        if (weapon instanceof SpinningSword) {
            iconImage = weaponImages[0];
        } else if (weapon instanceof Bow) {
            iconImage = weaponImages[1];
        } else if (weapon instanceof Aura) {
            iconImage = weaponImages[2];
        }
    }

    public LevelUpChoice(String name, int type, int abilityType, int abilityValue, Player player) {
        this.name = name;
        this.type = type;
        this.abilityType = abilityType;
        this.abilityValue = abilityValue;
        this.player = player;
        iconImage = abilityImages[abilityType - 1];
    }

    public String getName() { return name; }
    public Weapon getWeapon() { return weapon; }

    public void apply() {
        if (type == ADD_WEAPON) {
            player.getWeapons().add(weapon);
            weapon.setAttack(player.attack);
        } else if (type == UPGRADE_WEAPON) {
            weapon.levelUp();
        } else if (type == UPGRADE_PLAYER) {
            if (abilityType == UPGRADE_ATK) {
                player.addAttack(abilityValue);
            } else if (abilityType == UPGRADE_DEF) {
                player.defense += abilityValue;
            } else if (abilityType == UPGRADE_HP) {
                player.maxHp += abilityValue;
                player.hp = player.maxHp;
            } else if (abilityType == UPGRADE_SPD) {
                player.speed += abilityValue;
            }
        }
    }

    public LevelUpChoice nextUpgrade() {
        if (type == ADD_WEAPON) {
            return new LevelUpChoice(name + " Lv.2", UPGRADE_WEAPON, weapon, player);
        } else if (type == UPGRADE_WEAPON) {
            if (weapon.getLevel() == 5) return null;
            // add the last character of the name by 1
            name = name.substring(0, name.length() - 1) + (char)(name.charAt(name.length() - 1) + 1);
            return this;
        } else if (type == UPGRADE_PLAYER) {
            return this;
        }

        return null;
    }

    private static BufferedImage[] loadWeaponImages() {
        BufferedImage[] images = new BufferedImage[3];
        images[0] = ImageTools.rotateImage(ImageTools.readImage("/weapons/Sword.png"), -45);
        images[1] = ImageTools.rotateImage(ImageTools.readImage("/weapons/Bow.png"), -45);
        images[2] = ImageTools.readImage("/weapons/Aura.png");
        return images; 
    }

    private static BufferedImage[] loadAbilityImages() {
        BufferedImage[] images = new BufferedImage[4];
        // TODO: load ability images
        // images[0] = ImageTools.readImage("/icons/atk.png");
        // images[1] = ImageTools.readImage("/icons/def.png");
        // images[2] = ImageTools.readImage("/icons/hp.png");
        // images[3] = ImageTools.readImage("/icons/spd.png");

        // use string to represent ability icon
        images[0] = genTextImage("ATK");
        images[1] = genTextImage("DEF");
        images[2] = genTextImage("HP");
        images[3] = genTextImage("SPD");

        return images;
    }

    private static BufferedImage genTextImage(String text) {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        // g.setColor(Color.BLACK);
        // g.fillRect(0, 0, 50, 50);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString(text, 23 - text.length() * 5, 35);
        return img;
    }
}
