package listeners;

import api.*;
import entity.*;
import entity.enemy.*;
import event.*;

public class PlayerHurtListener implements EventListener {

    @EventHandler
    public void onPlayerHitByEnemy(EnemyHitPlayerEvent event) {
        Player player = event.getPlayer();
        Enemy enemy = event.getEnemy();
        // System.out.println("Player hit by enemy");
        player.colideWith(enemy);

    }

}
