package entity.enemy;

import java.awt.*;

import entity.Player;

public class Zombie extends Enemy {

    public Zombie(String name, int x, int y, int hp, int attack, int speed, Player player) {
        super(name, x, y, hp, attack, speed, player);
    }

    @Override
    public void draw(Graphics g) {
        // g.fillRect((int)(x - width/2), (int)(y - height/2), width, height);
        super.draw(g);
    }


}
