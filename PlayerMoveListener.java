import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerMoveListener implements KeyListener {

    public Player player;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public PlayerMoveListener(Player player) {
        this.player = player;
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
