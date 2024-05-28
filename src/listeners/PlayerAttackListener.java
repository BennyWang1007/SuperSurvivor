package listeners;

import api.*;
import weapons.SpinningSword;
import weapons.Weapon;
import entity.monster.Monster;
import event.*;

public class PlayerAttackListener implements EventListener {

    // TODO: implement the PlayerAttackListener

    @EventHandler
    public void onPlayerAttack(WeaponHitEnemyEvent event) {
        Weapon weapon = event.getWeapon();
        Monster enemy = event.getEnemy();
        weapon.attackOn(enemy);
    }

}
