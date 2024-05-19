package event;

import weapons.*;
import entity.enemy.Enemy;

public class WeaponHitEnemyEvent extends Event {
    
    private final Weapon weapon;
    private final Enemy enemy;

    public WeaponHitEnemyEvent(Weapon weapon, Enemy enemy) {
        super("WeapomHitEnemyEvent");
        this.weapon = weapon;
        this.enemy = enemy;
        if (weapon instanceof SpinningSword) {
            SpinningSword spinningSword = (SpinningSword) weapon;
            spinningSword.attackOn(enemy);
        }
    }

    public Weapon getWeapon() { return weapon; }
    public Enemy getEnemy() { return enemy; }
    
}
