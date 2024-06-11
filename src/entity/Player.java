package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import entity.monster.Monster;
import main.Game;
import main.MapTile;
import main.SoundType;
import utils.ImageTools;
import weapons.*;

public class Player extends Entity{
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    private float speed;
    private float speedPerFrame;
    public int exp;
    public int level;
    public final int maxLevel = 39;
    public int score;
    public int[] expTable = {0, 1, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,
                             1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900,
                             2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800,
                             2900, 3000, 3100, 3200, 3300, 3400, 3500, 3600, 3700, 3800};

    public int damageCooldown;
    private int curMaxDamage;

    private Set<Weapon> weapons;

    public float collectRange = 150f;

    private BufferedImage up1,up2,down1,down2,right1,right2,left1,left2;
    private BufferedImage currentPlayerImage;
    private int tileCounter = 0;
    private boolean isFrame1 = true;
    private final int framesToChange = Game.FPS / 5;

    public Player(Game game, String name, int x, int y) {
        super(game, x, y, 50, 50);
        this.name = name;
        this.speed = 250;
        this.speedPerFrame = speed / Game.FPS;
        this.attack = 20;
        this.hp = 500;
        this.maxHp = 500;
        this.defense = 0;
        this.exp = 0;
        this.level = 1;
        this.score = 0;
        this.damageCooldown = 0;
        this.weapons = new HashSet<>();
        getPlayerImage();
    }

    public void init() {
        this.speed = 250;
        this.speedPerFrame = speed / Game.FPS;
        this.attack = 20;
        this.hp = 500;
        this.maxHp = 500;
        this.defense = 0;
        this.exp = 0;
        this.level = 1;
        this.score = 0;
        this.damageCooldown = 0;
        weapons.clear();
        tileCounter = 0;
        isFrame1 = true;
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

    public void processMoveTile() {
        tileCounter++;
        if (tileCounter >= framesToChange) {
            isFrame1 = !isFrame1;
            tileCounter = 0;
        }
    }
    
    public void move(float deltaX, float deltaY, String direction) {
        processMoveTile();
        switch (direction) {
            case "up":
                currentPlayerImage = (isFrame1 ? up1 : up2);
                break;
            case "down":
                currentPlayerImage = (isFrame1 ? down1 : down2);
                break;
            case "left":
                currentPlayerImage = (isFrame1 ? left1 : left2);
                break;
            case "right":
                currentPlayerImage = (isFrame1 ? right1 : right2);
                break;
        }
        float nextX = x + deltaX;
        float nextY = y + deltaY;
        if (!checkMapTileCollision(nextX, nextY)) move(nextX, nextY);
    }
    
    public void move(float x, float y) {
        if (game.isValidPosition(x, y)) {
            this.x = x;
            this.y = y;
        }
    }
    
    public void moveUp() { move(0, -speedPerFrame, "up"); }
    public void moveDown() { move(0, speedPerFrame, "down"); }
    public void moveLeft() { move(-speedPerFrame, 0, "left"); }
    public void moveRight() { move(speedPerFrame, 0, "right"); }

    public void moveLeftUp() {
        float delta = speedPerFrame / 1.414f;
        move(-delta, -delta, "left");
    }
    
    public void moveRightUp() {
        float delta = speedPerFrame / 1.414f;
        move(delta, -delta, "right");
    }
    
    public void moveLeftDown() {
        float delta =  speedPerFrame / 1.414f;
        move(-delta, delta, "left");
    }
    
    public void moveRightDown() {
        float delta = speedPerFrame / 1.414f;
        move(delta, delta, "right");
    }

    public void update() {
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
    public void addAura() { weapons.add(new Aura(game, 200, 200, 0.5f, 200, this)); }

    public void collideWith(Monster monster) {
        if (damageCooldown > 0) return;
        curMaxDamage = Math.max(curMaxDamage, monster.attack);
    }

    public void collideWith(Projectile projectile) {
        damage(projectile.attack);
    }

    private void takeDamage() {
        if (curMaxDamage == 0) return;
        damage(curMaxDamage);
        damageCooldown = Game.FPS / 2;
        curMaxDamage = 0;
    }

    private void damage(int damage) {
        game.playSound(SoundType.PLAYER_HURT);
        if (Game.GOD_MODE || damage <= defense) return;
        hp -= damage - defense;
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

    public void addSpeed(float speed) {
        this.speed += speed;
        this.speedPerFrame = this.speed / Game.FPS;
    }

    public float getSpeed() { return speed; }

    private void levelUp() {
        game.playSound(SoundType.LEVEL_UP);
        level++;
        int dHp = maxHp / 20;
        maxHp += dHp;
        hp += dHp;
        addAttack(5);
        defense += 2;
        game.levelUp();
    }

    public void addExp(int exp) { this.exp += exp;}
    public void addScore(int score) { this.score += score; }
    public int getScore() { return score; }

    private boolean checkMapTileCollision(float nextX, float nextY) {
        int playerLeft = (int) nextX - 16;
        int playerRight = (int) nextX + 16;
        int playerTop = (int) nextY;
        int playerBottom = (int) nextY + 24;
        int playerLeftCol = playerLeft / game.tileSize;
        int playerRightCol = playerRight / game.tileSize;
        int playerTopRow = playerTop / game.tileSize;
        int playerBottomRow = playerBottom / game.tileSize;

        int TileNum1, TileNum2;
        int[][] mapTileNum = game.getMapTileNum();
        MapTile[] mapTiles = game.getMapTiles();
        
        TileNum1 = mapTileNum[playerTopRow][playerLeftCol];
        TileNum2 = mapTileNum[playerTopRow][playerRightCol];
        if (mapTiles[TileNum1].collision || mapTiles[TileNum2].collision) {
            return true;
        }

        TileNum1 = mapTileNum[playerBottomRow][playerLeftCol];
        TileNum2 = mapTileNum[playerBottomRow][playerRightCol];
        if (mapTiles[TileNum1].collision || mapTiles[TileNum2].collision) {
            return true;
        }

        TileNum1 = mapTileNum[playerTopRow][playerLeftCol];
        TileNum2 = mapTileNum[playerBottomRow][playerLeftCol];
        if (mapTiles[TileNum1].collision || mapTiles[TileNum2].collision) {
            return true;
        }

        TileNum1 = mapTileNum[playerTopRow][playerRightCol];
        TileNum2 = mapTileNum[playerBottomRow][playerRightCol];
        if (mapTiles[TileNum1].collision || mapTiles[TileNum2].collision) {
            return true;
        }

        return false;
    }

    /**
     * Draw the weapons and then the player
     * @param g the Graphics object
     */
    public void draw(Graphics g) {
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

        if (Game.DEBUG) {
            getHitBox().draw(g);
        }

    }

    @Override
    public Hitbox getHitBox() {
        return new Hitbox((int)(x - width/3), (int)(y - height/3), (int)(x + width/3), (int)(y + height/3));
    }

}
