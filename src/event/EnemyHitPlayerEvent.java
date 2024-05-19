package event;

import entity.*;
import entity.enemy.Enemy;

public class EnemyHitPlayerEvent extends Event {
    private final Player player;
    private final Enemy enemy;

    public EnemyHitPlayerEvent(Player player, Enemy enemy) {
        super("EnemyHitPlayerEvent");
        this.player = player;
        this.enemy = enemy;
    }

    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
}
