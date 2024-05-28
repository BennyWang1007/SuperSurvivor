package listeners;

import api.*;
import entity.*;
import entity.monster.*;
import event.*;

public class PlayerHurtListener implements EventListener {

    @EventHandler
    public void onPlayerHitByEnemy(EnemyHitPlayerEvent event) {
        Player player = event.getPlayer();
        Monster enemy = event.getEnemy();
        // System.out.println("Player hit by enemy");
        player.collideWith(enemy);
    }

}
