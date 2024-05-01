
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game {
    public static void main(String[] args) {
        int SCREEN_WIDTH = 1080;
        int SCREEN_HEIGHT = 720;

        int FPS = 60;
        double TARGET_TIME = 1000000000 / FPS;

        JFrame frame = new JFrame("Game");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // JLabel playerName = new JLabel("Player 1");

        Player player = new Player("PlayerName", 100, 100, 5);
        player.setWidth(50);
        player.setHeight(50);
        
        JPanel playerPanel = new PlayerPanel(player);
        frame.add(playerPanel);
        // playerName.setText(player.name);
        // playerName.setBounds(player.x, player.y, 100, 50);
        // // playerName.setLocation(0, 0);
        // frame.add(playerName);

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
                playerPanel.repaint();
            }
        }

    }

    private static void setPlayerName(Player player) {
        String name = JOptionPane.showInputDialog("Enter player name");
        player.setName(name);
        // playerName.setText(name);
        // JTextField textField = new JTextField(50);
        // JButton okBtn = new JButton("OK");
        // okBtn.addActionListener(e -> {
        //     player.setName(textField.getText());
        // });
    }

    // private static void moveUp(Object obj) {
    //     System.out.println("Moving up");
    //     if (obj instanceof JLabel) {
    //         int x = ((JLabel) obj).getX();
    //         int y = ((JLabel) obj).getY();
    //         ((JLabel) obj).setBounds(x, y - 10, 100, 50);
    //     } else if (obj instanceof JButton) {
    //         int x = ((JButton) obj).getX();
    //         int y = ((JButton) obj).getY();
    //         ((JButton) obj).setBounds(x, y - 10, 100, 50);
    //     }
    // }

}