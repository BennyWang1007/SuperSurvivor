package weapons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import entity.Player;
import entity.monster.Monster;
import main.Game;

public class SpinningSword extends Weapon{
    
    private float degreePerSecond;
    private float distance;
    private float degree;

    public SpinningSword(Game game, int width, int height, int attack, float degreePerSecond, float distance, Player owner){
        super(game, width, height, attack, owner);
        this.degreePerSecond = degreePerSecond;
        this.distance = distance;
        this.degree = 0;
        readImage("/Sword.png");
        cooldownTime = 0.5f;
    }

    public void update() {
        degree += degreePerSecond / Game.FPS;
        if (degree >= 360) { degree -= 360; }

        int offsetX = (int)(Math.cos(Math.toRadians(degree)) * distance);
        int offsetY = (int)(Math.sin(Math.toRadians(degree)) * distance);
        x = player.x + offsetX;
        y = player.y + offsetY;
        decreaseCooldowns();
    }

    

    public void attackOn(Monster monster) {
        if (attackCooldowns.containsKey(monster.id)) {
            return;
        }
        monster.damage(attack);
        attackCooldowns.put(monster.id, Game.FPS);
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing weapon");
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        AffineTransform at = AffineTransform.getTranslateInstance(cx, cy);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);
        ((Graphics2D) g).drawImage(image, at, null);

        // draw hitbox
//        g.setColor(Color.RED);
//        ((Graphics2D) g).drawRect(cx, cy, width, height);
    }

    public void loadAnimation() {}

    @Override
    public void levelUp() {
        level++;
        player.addSpinningSword();
        // set every degree of spinning sword
        degree = 0;
        ArrayList<SpinningSword> swords = player.getSwords();
        for (int i = 0; i < swords.size(); i++) {
            swords.get(i).degree = 360 / swords.size() * i;
        }
    }
}