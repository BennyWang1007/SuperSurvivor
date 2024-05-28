package event;

import entity.*;
import entity.monster.Monster;

public class EnemyHitPlayerEvent extends Event {
    private final Player player;
    private final Monster enemy;

    public EnemyHitPlayerEvent(Player player, Monster enemy) {
        super("EnemyHitPlayerEvent");
        this.player = player;
        this.enemy = enemy;
    }

    public Player getPlayer() { return player; }
    public Monster getEnemy() { return enemy; }
}
