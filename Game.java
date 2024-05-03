
import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        int SCREEN_WIDTH = 1080;
        int SCREEN_HEIGHT = 720;

        int FPS = 60;
        double TARGET_TIME = 1000000000 / FPS;

        JFrame frame = new JFrame("Game");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Player player = new Player("PlayerName", 100, 100, 5);
        player.setWidth(50);
        player.setHeight(50);
        
        GamePanel gamePanel = new GamePanel(player);
        gamePanel.addMonster(new Monster("Monster1", 800, 400, 1));
        gamePanel.addMonster(new Monster("Monster2", 200, 500, 1));
        frame.add(gamePanel);

        JMenu menu = new JMenu("File");
        JMenuItem menuItem = new JMenuItem("Set Name");
        menuItem.addActionListener(e -> setPlayerName(player));
        menu.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem("Save");
        menuItem2.addActionListener(e -> System.out.println("Save clicked"));
        menu.add(menuItem2);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        frame.addKeyListener(new PlayerMoveListener(player));
        
        frame.setVisible(true);
        
        // game loop
        long start = System.nanoTime();
        while (true) {
            long elapsed = System.nanoTime() - start;
            if (elapsed > TARGET_TIME) {
                start = System.nanoTime();
                gamePanel.update();
                gamePanel.repaint();
            }
        }

    }

    private static void setPlayerName(Player player) {
        String name = JOptionPane.showInputDialog("Enter player name");
        player.setName(name);
    }

}