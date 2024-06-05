package weapons;

import java.awt.Graphics;
import java.util.Set;
import java.util.HashSet;

import entity.Player;
import entity.monster.Monster;
import main.Game;
import listeners.GameMouseListener;

public class Bow extends Weapon {

    private float speed; // in pixels per second
    private float shotInterval; // in seconds
    private int shotCooldown = 0;
    private Set<Arrow> arrows = new HashSet<Arrow>();

    public Bow(Game game, int width, int height, int attack, float speed, float shotInterval, Player player) {
        super(game, width, height, attack, player);
        this.speed = speed;
        this.shotInterval = shotInterval;
        // readImage("res/bow.png");
    }

    @Override
    public void update() {

        for (Arrow arrow : arrows) {
            arrow.update();
        }
        arrows.removeIf(arrow -> arrow.toDelete);

        if (shotCooldown > 0 || arrows.size() > 20) {
            shotCooldown--;
            return;
        }

        // shoot towards mouse
        GameMouseListener mouseListener = game.getMouseListener();
        int centerX = game.translateToScreenX(player.x);
        int centerY = game.translateToScreenY(player.y);
        float degree = (float) Math.toDegrees(Math.atan2(mouseListener.mouseY - centerY, mouseListener.mouseX - centerX));
        arrows.add(new Arrow(game, player.x, player.y, 40, 40, attack, degree, speed / Game.FPS, player));
        shotCooldown = (int) (shotInterval * Game.FPS);

        // // find nearest monster
        // Monster nearestMonster = null;
        // double minDistance = Double.MAX_VALUE;
        // Set<Monster> monsters = game.getMonsters();
        // for (Monster monster : monsters) {
        //     double distance = Math.hypot(monster.x - player.x, monster.y - player.y);
        //     if (distance < minDistance) {
        //         minDistance = distance;
        //         nearestMonster = monster;
        //     }
        // }
        // if (nearestMonster != null) {
        //     float degree = (float) Math.toDegrees(Math.atan2(nearestMonster.y - player.y, nearestMonster.x - player.x));
        //     arrows.add(new Arrow(game, player.x, player.y, 40, 40, attack, degree, speed / Game.FPS, player));
        //     shotCooldown = (int) (shotInterval * Game.FPS);
        // }
    }

    @Override
    public void attackOn(Monster monster) {
        // Do nothing
    }

    @Override
    public void draw(Graphics g) {
        for (Arrow arrow : arrows) {
            arrow.draw(g);
        }
    }

    @Override
    public void loadAnimation() {
        // TODO
    }

    @Override
    public void levelUp() {
        level++;
        shotInterval *= 0.6;
    }

}
