package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import entity.monster.Monster;
import main.Game;
import main.MapTile;
import main.Sound;
import utils.ImageTools;
import weapons.*;

public class Player extends Entity{
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    public float speed;
    public int exp;
    public int level;
    public final int maxLevel = 39;
    // public int[] expTable = {0, 100, 200, 400, 800, 1600, 3200, 6400};
    public int[] expTable = {0, 100, 100, 100, 100, 100, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 320, 340, 360, 380, 400, 420, 440, 460, 480, 500, 520, 540, 560, 580, 600, 620, 640, 660, 680, 700, 720, 740, 760, 780, 800};

    public int damageCooldown;
    private int curMaxDamage;

    private Set<Weapon> weapons;

    public float collectRange = 150f;

    private BufferedImage up1,up2,down1,down2,right1,right2,left1,left2;
    private BufferedImage currentPlayerImage;
    private int tileCounter = 0;
    private int tileNum = 1;
    private final int framesToChange = Game.FPS / 5;

    private Sound levelUpSound;
    private Sound hurtSound;

    public Player(Game game, String name, int x, int y) {
        super(game, x, y, 50, 50);
        this.name = name;
        this.speed = 250;
        this.attack = 20;
        this.hp = 500;
        this.maxHp = 500;
        this.defense = 0;
        this.exp = 0;
        this.level = 1;
        this.damageCooldown = 0;
        this.weapons = new HashSet<>();
        levelUpSound = new Sound("level_up.wav");
        hurtSound = new Sound("player_hurt.wav");
        getPlayerImage();
    }

    public void init() {
        this.speed = 250;
        this.attack = 20;
        this.hp = 500;
        this.maxHp = 500;
        this.defense = 0;
        this.exp = 0;
        this.level = 1;
        this.damageCooldown = 0;
        weapons.clear();
        tileCounter = 0;
        tileNum = 1;
        damageCooldown = 0;
        curMaxDamage = 0;
        currentPlayerImage = down1;
    }

    private void getPlayerImage(){
        up1 = ImageTools.scaleImage(ImageTools.readImage("/player/forward1.png"), width, height);
        up2 = ImageTools.scaleImage(ImageTools.readImage("/player/forward2.png"), width, height);
        down1 = ImageTools.scaleImage(ImageTools.readImage("/player/backward1.png"), width, height);
        down2 = ImageTools.scaleImage(ImageTools.readImage("/player/backward2.png"), width, height);
        left1 = ImageTools.scaleImage(ImageTools.readImage("/player/left1.png"), width, height);
        left2 = ImageTools.scaleImage(ImageTools.readImage("/player/left2.png"), width, height);
        right1 = ImageTools.scaleImage(ImageTools.readImage("/player/right1.png"), width, height);
        right2 = ImageTools.scaleImage(ImageTools.readImage("/player/right2.png"), width, height);
        currentPlayerImage = down1;
    }

    public void moveUp() {
        tileCounter++;
        if (tileCounter >= framesToChange) {
            tileNum = (tileNum == 1 ? 2 : 1);
            tileCounter = 0;
        }
        currentPlayerImage = (tileNum == 1 ? up1 : up2);
        if (checkMapTileCollision("up")) return;
        move(x, (float)(y - speed * Game.DELTA_TIME));
    }

    public void moveDown() {
        tileCounter++;
        if (tileCounter >= framesToChange) {
            tileNum = (tileNum == 1 ? 2 : 1);
            tileCounter = 0;
        }
        currentPlayerImage = (tileNum == 1 ? down1 : down2);
        if (checkMapTileCollision("down")) return;
        move(x, (float)(y + speed * Game.DELTA_TIME));
    }

    public void moveLeft() {
        tileCounter++;
        if (tileCounter >= framesToChange) {
            tileNum = (tileNum == 1 ? 2 : 1);
            tileCounter = 0;
        }
        currentPlayerImage = (tileNum == 1 ? left1 : left2);
        if (checkMapTileCollision("left")) return;
        move((float)(x - speed * Game.DELTA_TIME), y);
    }

    public void moveRight() {
        tileCounter++;
        if (tileCounter >= framesToChange) {
            tileNum = (tileNum == 1 ? 2 : 1);
            tileCounter = 0;
        }
        currentPlayerImage = (tileNum == 1 ? right1 : right2);
        if (checkMapTileCollision("right")) return;
        move((float)(x + speed * Game.DELTA_TIME), y);
    }

    public void move(float x, float y) {
        if (game.isValidPosition(x, y)) {
            this.x = x;
            this.y = y;
        }
    }

    public void update() {
        // System.out.println("\rPlayer at " + x + ", " + y);
        damageCooldown--;
        weapons.forEach(Weapon::update);
        if (damageCooldown > 0) return;
        takeDamage();
        if (level < maxLevel && exp >= expTable[level]) {
            exp -= expTable[level];
            levelUp();
        }
    }

    public Set<Weapon> getWeapons() { return weapons; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public <T extends Weapon> T getWeapon(Class<T> weaponType) {
        for (Weapon weapon : weapons) {
            if (weaponType.isInstance(weapon)) {
                return weaponType.cast(weapon);
            }
        }
        return null;
    }

    public Aura getAura() { return getWeapon(Aura.class); }
    public SpinningSword getSpinningSword() { return getWeapon(SpinningSword.class); }
    public Bow getBow() { return getWeapon(Bow.class); }

    public void addBow() { weapons.add(new Bow(game, 100, 100, 5, 360, 1, this)); }
    public void addSpinningSword() { weapons.add(new SpinningSword(game, 100, 100, 1, 300, 100, this)); }
    public void addAura() { weapons.add(new Aura(game, 200, 200, 0.5f, 100, this)); }

    public void collideWith(Monster monster) {
        if (damageCooldown > 0) return;
        curMaxDamage = Math.max(curMaxDamage, monster.attack);
    }

    public void collideWith(Projectile projectile) {
        damage(projectile.attack);
    }

    private void takeDamage() {
        if (curMaxDamage == 0) return;
        if (game.settings.isSoundOn()) hurtSound.play();
        damage(curMaxDamage);
        damageCooldown = Game.FPS / 2;
        curMaxDamage = 0;
    }

    private void damage(int damage) {
        if (damage <= defense) return;
        hp -= damage - defense;
        // if (hp < 0) hp = 0;
    }

    public void heal(int heal) {
        hp += heal;
        if (hp > maxHp) hp = maxHp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
        for (Weapon weapon : weapons) {
            weapon.setAttack(this.attack);
        }
    }
    public void addAttack(int attack) {
        this.attack += attack;
        for (Weapon weapon : weapons) {
            weapon.setAttack(this.attack);
        }
    }

    private void levelUp() {
        if (game.settings.isSoundOn()) levelUpSound.play();
        level++;
        maxHp += 10;
        hp = maxHp;
        addAttack(5);
        defense += 2;
        game.levelUp();
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    private boolean checkMapTileCollision(String direction) {
        int playerLeft = (int) x - 16;
        int playerRight = (int) x + 16;
        int playerTop = (int) y;
        int playerButtom = (int) y + 24;
        int playerLeftCol = playerLeft / game.tileSize;
        int playerRightCol = playerRight / game.tileSize;
        int playerTopRow = playerTop / game.tileSize;
        int playerButtomRow = playerButtom / game.tileSize;

        int tileNum1, tileNum2;
        int[][] mapTileNum = game.getMapTileNum();
        MapTile[] mapTiles = game.getMapTiles();
        switch (direction) {
            case "up":
                playerTopRow = (playerTop - (int)(speed * Game.DELTA_TIME)) / game.tileSize;
                tileNum1 = mapTileNum[playerTopRow][playerLeftCol];
                tileNum2 = mapTileNum[playerTopRow][playerRightCol];
                if (mapTiles[tileNum1].collision || mapTiles[tileNum2].collision) {
                    return true;
                }
                break;
            case "down":
                playerButtomRow = (playerButtom +(int)(speed * Game.DELTA_TIME)) / game.tileSize;
                tileNum1 = mapTileNum[playerButtomRow][playerLeftCol];
                tileNum2 = mapTileNum[playerButtomRow][playerRightCol];
                if (mapTiles[tileNum1].collision || mapTiles[tileNum2].collision) {
                    return true;
                }
                break;
            case "right":
                playerRightCol = (playerRight +(int)(speed * Game.DELTA_TIME)) / game.tileSize;
                tileNum1 = mapTileNum[playerTopRow][playerRightCol];
                tileNum2 = mapTileNum[playerButtomRow][playerRightCol];
                if (mapTiles[tileNum1].collision || mapTiles[tileNum2].collision) {
                    return true;
                }
                break;
            case "left":
                playerLeftCol = (playerLeft -(int)(speed * Game.DELTA_TIME)) / game.tileSize;
                tileNum1 = mapTileNum[playerTopRow][playerLeftCol];
                tileNum2 = mapTileNum[playerButtomRow][playerLeftCol];
                if (mapTiles[tileNum1].collision || mapTiles[tileNum2].collision) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Draw the weapons and then the player
     * @param g the Graphics object
     */
    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        int drawX = (screenX - width/2);
        int drawY = (screenY - height/2);
        g.setFont(g.getFont().deriveFont(12f));
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        weapons.forEach(weapon -> weapon.draw(g));
        g.setColor(java.awt.Color.BLUE);
        g.drawImage(currentPlayerImage, drawX, drawY, width, height, null);
        g.drawString(name, drawX, drawY - 5);
        
        // Draw health bar
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = drawX;
        int healthBarY = drawY - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
    }

}
