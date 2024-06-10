package main;

import entity.Player;
import entity.monster.KnifeGoblin;
import entity.monster.Monster;
import entity.monster.Roller;
import entity.monster.StoneThrower;

import java.util.Random;
import java.util.Set;

public class MonsterSpawner {
    private final Game game;
    private Player player;
    private Set<Monster> monsters;
    private Random random = new Random();

    private final int minRadius = 400;
    private final int maxRadius = 1000;

    public MonsterSpawner(Game game, Player player, Set<Monster> monsters) {
        this.game = game;
        this.player = player;
        this.monsters = monsters;
    }

    public void spawnMonster(int id, double strength) {
        int[] pos = getSpawnPositionRandomly();
        Monster monster;
        float rand = random.nextFloat();
        if (rand < 0.5) {
            monster = new KnifeGoblin(game, "Monster", pos[0], pos[1], strength, player);
        } else if (rand < 0.9) {
            monster = new StoneThrower(game, "Monster", pos[0], pos[1], strength, player);
        } else {
            monster = new Roller(game, "Monster", pos[0], pos[1], strength, player);
        }
        monster.setId(id);
        monsters.add(monster);
    }

    private int[] getSpawnPositionRandomly() {
        int[] pos = new int[2];
        int radius = random.nextInt(maxRadius-minRadius) + minRadius;
        int degree = random.nextInt(360);

        int deltaX = (int) (radius * Math.cos(Math.toRadians(degree)));
        int deltaY = (int) (radius * Math.sin(Math.toRadians(degree)));

        pos[0] = (int) player.getX() + deltaX;
        pos[1] = (int) player.getY() + deltaY;

        return pos;
    }
}
