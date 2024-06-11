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

    public void setPause(boolean pause) { isPause = pause; }

    public void reset() {
        isPause = false;
        escPressed = false;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

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

        // secret key
        if (e.isControlDown() && e.isShiftDown() && e.isAltDown() && e.getKeyCode() == KeyEvent.VK_G) {
            Game.DEBUG = !Game.DEBUG;
            // System.out.println("Debug mode: " + Game.DEBUG);
        }

        if (Game.DEBUG && e.getKeyCode() == KeyEvent.VK_F10) {
            Game.GOD_MODE = !Game.GOD_MODE;
        }

        if (Game.DEBUG && e.getKeyCode() == KeyEvent.VK_F9) {
            player.heal(10000000);
        }

        if (Game.DEBUG && e.getKeyCode() == KeyEvent.VK_F8) {
            player.exp = player.expTable[player.level];
        }
        
        if (Game.DEBUG && e.getKeyCode() == KeyEvent.VK_K) {
            game.getMonsters().clear();
            game.getMonsterProjectiles().clear();
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
        if (leftPressed) {
            if (upPressed) player.moveLeftUp();
            else if (downPressed) player.moveLeftDown();
            else player.moveLeft();
        }
        else if (rightPressed) {
            if (upPressed) player.moveRightUp();
            else if (downPressed) player.moveRightDown();
            else player.moveRight();
        } else {
            if (upPressed) player.moveUp();
            else if (downPressed) player.moveDown();
        }
    }
    
}
