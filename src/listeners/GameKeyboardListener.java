package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import entity.Player;
import main.GamePanel;

public class GameKeyboardListener implements KeyListener {

    public Player player;
    public GamePanel gamePanel;
    private final int FPS;

    private boolean isPause = false;
    private boolean escPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private final int sleepNano;
    private final long sleepMilli;

    public GameKeyboardListener(GamePanel gamePanel, Player player) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.FPS = gamePanel.getFPS();
        sleepNano = (1000000000 / FPS) % 1000000;
        sleepMilli = (1000000000 / FPS) / 1000000;
        System.out.println("Key listener created with FPS: " + FPS + "(" + sleepMilli + "ms " + sleepNano + "ns)");
        // new Thread(() -> {
        //     while (true) {
        //         if (upPressed) {
        //             player.moveUp();
        //         }
        //         if (downPressed) {
        //             player.moveDown();
        //         }
        //         if (leftPressed) {
        //             player.moveLeft();
        //         }
        //         if (rightPressed) {
        //             // System.out.println("right pressed");
        //             player.moveRight();
        //         }
        //         try {
        //             Thread.sleep(sleepMilli, sleepNano);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }).start();
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

    public void update() {
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
    }
    
}
