import javax.swing.*;

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
public class GameController {

    private GameModel model;
    private GameView view;
    private JFrame frame;
    private static final int FPS = 60;  // Target frames per second

    /**
     * Constructor - initializes the controller and sets up the game
     */
    public GameController() {
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

        // TODO: Add keyboard input listener
        // TODO: Start game loop timer
    }

    /**
     * Start the game loop
     * Uses a Timer to update the model and view at regular intervals
     */
    private void startGameLoop() {
        // TODO: Create and start a Swing Timer
        // TODO: Timer should call model.update() and view.updateDisplay()
        // TODO: Set timer delay based on FPS (1000 / FPS milliseconds)
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
