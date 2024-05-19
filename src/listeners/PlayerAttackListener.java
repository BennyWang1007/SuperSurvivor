package listeners;

import api.*;
import weapons.SpinningSword;
import weapons.Weapon;
import entity.enemy.Enemy;
import event.*;

public class PlayerAttackListener implements EventListener {

    // TODO: implement the PlayerAttackListener

    @EventHandler
    public void onPlayerAttack(WeaponHitEnemyEvent event) {
        Weapon weapon = event.getWeapon();
        Enemy enemy = event.getEnemy();
        if (weapon instanceof SpinningSword) {
            SpinningSword spinningSword = (SpinningSword) weapon;
            spinningSword.attackOn(enemy);
        }
    }

}
