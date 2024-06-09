package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import entity.Player;
import main.Game;
import main.GameState;

public class GameKeyboardListener implements KeyListener {

    private final Game game;
    private final Player player;

    private boolean isPause = false;
    private boolean escPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameKeyboardListener(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // System.out.println("Key typed" + e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (game.getGameState() ==  GameState.TITLE_SCREEN) return;
        if (game.getGameState() == GameState.LEVEL_UP) return;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !escPressed) {
            isPause = !isPause;
            if (isPause) game.pause();
            else game.resume();
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (game.getGameState() ==  GameState.TITLE_SCREEN) {
            return;
        }
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
