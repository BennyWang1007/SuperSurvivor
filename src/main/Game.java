package main;

import javax.swing.*;
import java.awt.*;

public class Game {
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int SCREEN_HEIGHT = screenSize.height;
        int SCREEN_WIDTH = SCREEN_HEIGHT * 16 / 9;
//        int SCREEN_WIDTH = 1080;
//        int SCREEN_HEIGHT = 720;
        int FPS = 60;
        double TARGET_TIME = 1000000000 / FPS;

        JFrame frame = new JFrame("Game");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel gamePanel = new GamePanel();
        Player player = new Player("PlayerName", 100, 100, 5, gamePanel);
        player.setWidth(50);
        player.setHeight(50);
        gamePanel.setPlayer(player);
        gamePanel.setFPS(FPS);
        frame.add(gamePanel);

        // JMenu menu = new JMenu("File");
        // JMenuItem menuItem = new JMenuItem("Set Name");
        // menuItem.addActionListener(e -> setPlayerName(player));
        // menu.add(menuItem);
        // JMenuItem menuItem2 = new JMenuItem("Save");
        // menuItem2.addActionListener(e -> System.out.println("Save clicked"));
        // menu.add(menuItem2);
        // JMenuBar menuBar = new JMenuBar();
        // menuBar.add(menu);
        // frame.setJMenuBar(menuBar);

        frame.addKeyListener(new GameKeyboardListener(gamePanel, player));
        frame.setVisible(true);
        
        gamePanel.initGame();
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

        // // test the limit fps 
        // long start = System.nanoTime();
        // long lastTime = System.nanoTime();
        // long timer = System.nanoTime();
        // double delta = 0;
        // double ns = 1000000000 / FPS;
        // int frames = 0;
        // int updates = 0;
            
        // while (true) {
        //     long now = System.nanoTime();
        //     delta += (now - lastTime) / ns;
        //     lastTime = now;
        //     while (delta >= 1) {
        //         gamePanel.update();
        //         updates++;
        //         delta--;
        //     }
        //     gamePanel.repaint();
        //     frames++;
        //     if (System.nanoTime() - timer > 1000000000) {
        //         timer += 1000000000;
        //         System.out.println("FPS: " + frames + " UPS: " + updates);
        //         frames = 0;
        //         updates = 0;
        //     }
        // }

    }

    // private static void setPlayerName(Player player) {
    //     String name = JOptionPane.showInputDialog("Enter player name");
    //     player.setName(name);
    // }

}