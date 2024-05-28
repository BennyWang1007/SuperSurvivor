package event;

import weapons.*;
import entity.monster.Monster;

public class WeaponHitEnemyEvent extends Event {
    
    private final Weapon weapon;
    private final Monster enemy;

    public WeaponHitEnemyEvent(Weapon weapon, Monster enemy) {
        super("WeaponHitEnemyEvent");
        this.weapon = weapon;
        this.enemy = enemy;
    }

    public Weapon getWeapon() { return weapon; }
    public Monster getEnemy() { return enemy; }
    
}
