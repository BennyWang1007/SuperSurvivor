
import javax.swing.JPanel;
import java.awt.Graphics;


public class GamePanel extends JPanel{

    private Player player;

    private int monsterCount;
    private int maxMonsterCount;
    private Monster[] monsters;

    private int panelHeight;
    private int panelWidth;

    public GamePanel(Player player) {
        super();
        this.player = player;
        monsterCount = 0;
        maxMonsterCount = 100;
        monsters = new Monster[maxMonsterCount];
        player.setMonsters(monsters);

    }

    public void addMonster(Monster monster) {
        if (monsterCount < maxMonsterCount) {
            monsters[monsterCount] = monster;
            monsterCount++;
            player.setMonsterCount(monsterCount);
        }
    }

    public void removeMonster(Monster monster) {
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i] == monster) {
                for (int j = i; j < monsterCount - 1; j++) {
                    monsters[j] = monsters[j + 1];
                }
                monsterCount--;
                player.setMonsterCount(monsterCount);
                break;
            }
        }
    }

    public void update() {
        panelHeight = this.getHeight();
        panelWidth = this.getWidth();

        player.update();
        for (Weapon weapon : player.weapons) {
            weapon.update();
        }
        for (int i = 0; i < monsterCount; i++) {
            monsters[i].update(player.x, player.y);
        }

        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].isDead()) {
                // removeMonster(monsters[i]);
                int randX = (int)(Math.random() * panelWidth);
                int randY = (int)(Math.random() * panelHeight);
                monsters[i] = new Monster("Monster", randX, randY, 1);
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // System.out.println("Painting player");
        
        // draw : monster -> weapon -> player
        for (int i = 0; i < monsterCount; i++) {
            monsters[i].draw(g);
        }
        // for (Weapon weapon : player.weapons) {
        //     weapon.draw(g);
        // }
        player.draw(g);
    }
}
