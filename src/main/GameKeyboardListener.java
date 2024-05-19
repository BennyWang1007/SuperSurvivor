package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyboardListener implements KeyListener {

    public Player player;
    public GamePanel gamePanel;

    private boolean isPause = false;
    private boolean escPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameKeyboardListener(GamePanel gamePanel, Player player) {
        this.player = player;
        this.gamePanel = gamePanel;
        // System.out.println("Key listener created");
        new Thread(() -> {
            while (true) {
                if (upPressed) {
                    player.moveUp();
                }
                if (downPressed) {
                    player.moveDown();
                }
                if (leftPressed) {
                    player.moveLeft();
                }
                if (rightPressed) {
                    player.moveRight();
                }
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        // System.out.println("Key typed" + e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("Key pressed");
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !escPressed) {
            isPause = !isPause;
            gamePanel.reversePause();
            escPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            rightPressed = true;
        }
        // System.out.println("wasd: " + upPressed + downPressed + leftPressed + rightPressed);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // System.out.println("Key released");
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
    
}
