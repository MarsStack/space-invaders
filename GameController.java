import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * GameController.java
 *
 * Controller class for Space Invaders game.
 * Contains the main method and orchestrates the entire application.
 * Wires together GameModel and GameView.
 * Manages the game loop and user input handling.
 *
 * Responsibilities:
 * - Initialize the game window (JFrame)
 * - Create and wire GameModel and GameView
 * - Manage the game loop (timer-based updates)
 * - Handle keyboard input from the player
 * - Coordinate updates between model and view
 */
public class GameController implements KeyListener {

    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer gameLoop;
    private static final int FPS = 60;  // Target frames per second
    private static final int TIMER_DELAY = 1000 / FPS;  // ~16.67 ms per frame

    // Track which keys are currently pressed
    private Set<Integer> pressedKeys;

    /**
     * Constructor - initializes the controller and sets up the game
     */
    public GameController() {
        this.pressedKeys = new HashSet<>();

        // Create model
        this.model = new GameModel();

        // Create view
        this.view = new GameView(model);

        // Create and configure the JFrame
        this.frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);  // Center on screen
        frame.setVisible(true);

        // Add keyboard input listener
        view.addKeyListener(this);
        view.setFocusable(true);
        view.requestFocusInWindow();
    }

    /**
     * Start the game loop
     * Uses a Timer to update the model and view at regular intervals
     */
    private void startGameLoop() {
        gameLoop = new Timer(TIMER_DELAY, e -> updateGame());
        gameLoop.start();
    }

    /**
     * Update the game each frame
     * Handles input, updates model, and redraws view
     */
    private void updateGame() {
        // Handle continuous key presses
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            model.movePlayerLeft();
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            model.movePlayerRight();
        }

        // Update game model
        model.update();

        // Redraw view
        view.updateDisplay();

        // Stop the game loop if game is over
        if (!model.isGameRunning()) {
            gameLoop.stop();
        }
    }

    /**
     * KeyListener implementation - called when a key is pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        // Handle spacebar to fire
        if (keyCode == KeyEvent.VK_SPACE) {
            model.fireProjectile();
        }
    }

    /**
     * KeyListener implementation - called when a key is released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    /**
     * KeyListener implementation - called for key characters
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    /**
     * Main method - entry point for the application
     */
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.startGameLoop();
        });
    }
}
