package main;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import entity.*;
import entity.monster.Monster;
import listeners.GameMouseListener;
import weapons.*;

public class GamePanel extends Canvas {

    private final Game game;
    private Player player;
    private Set<Monster> monsters;
    private Set<DropItem> dropItems;
    private Set<Projectile> projectiles;

    private ArrayList<LevelUpChoice> levelUpChoices;
    private LevelUpChoice[] curLevelUpChoices = new LevelUpChoice[3];

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
            g.setColor(Color.BLACK);
            g.drawRoundRect(boxX[i], boxY, boxWidth, boxHeight, 10, 10);
            g.drawString(curLevelUpChoices[i].getName(), boxX[i] + boxWidth / 2 - curLevelUpChoices[i].getName().length() * 5, boxY + boxHeight / 2);
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
    
}

class LevelUpChoice {
    private String name;
    private final Player player;
    public final int type; // 1: Add weapon, 2: Upgrade weapon, 3. Upgrade player ability
    private Weapon weapon;
    private int abilityType;
    private int abilityValue;

    public static final int ADD_WEAPON = 1;
    public static final int UPGRADE_WEAPON = 2;
    public static final int UPGRADE_PLAYER = 3;

    public static final int UPGRADE_ATK = 1;
    public static final int UPGRADE_DEF = 2;
    public static final int UPGRADE_HP = 3;
    public static final int UPGRADE_SPD = 4;

    public LevelUpChoice(String name, int type, Weapon weapon, Player player) {
        this.name = name;
        this.type = type;
        this.weapon = weapon;
        this.player = player;
    }

    public LevelUpChoice(String name, int type, int abilityType, int abilityValue, Player player) {
        this.name = name;
        this.type = type;
        this.abilityType = abilityType;
        this.abilityValue = abilityValue;
        this.player = player;
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
}
