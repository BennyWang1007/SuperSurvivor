
import javax.swing.JPanel;
import java.awt.Graphics;


public class PlayerPanel extends JPanel{

    private Player player;

    public PlayerPanel(Player player) {
        super();
        this.player = player;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // System.out.println("Painting player");
        player.update();
        int x = player.x + player.width / 2;
        int y = player.y + player.height / 2;
        int width = player.width;
        int height = player.height;
        Weapons[] weapons = player.weapons;
        for (Weapons weapon : weapons) {
            weapon.paintComponent(g);
        }
        g.drawRect(x, y, width, height);
        g.drawString(player.name, x, y - 5);
    }
}
