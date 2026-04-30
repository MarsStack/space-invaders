import javax.swing.*;
import java.awt.*;

/**
 * GameView.java
 *
 * View class for Space Invaders game.
 * Extends JPanel to render the game visuals.
 * Responsible for all drawing operations and UI rendering.
 *
 * Responsibilities:
 * - Render the game board background
 * - Draw player spaceship
 * - Draw enemies
 * - Draw projectiles
 * - Display score and game status
 * - Handle graphics rendering
 */
public class GameView extends JPanel {

    private GameModel model;

    /**
     * Constructor - initializes the view with a reference to the model
     * @param model the game model to render
     */
    public GameView(GameModel model) {
        this.model = model;

        // Set preferred size
        setPreferredSize(new Dimension(model.getGameWidth(), model.getGameHeight()));

        // Set background color
        setBackground(Color.BLACK);

        // Enable double buffering by default in JPanel
        setDoubleBuffered(true);
    }

    /**
     * Paint the game - called automatically by Swing
     * Override paintComponent to draw all game entities
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw game entities
        drawPlayer(g2d);
        drawAliens(g2d);
        drawBullets(g2d);
        drawHUD(g2d);

        // Draw game-over message if game has ended
        if (!model.isGameRunning()) {
            drawGameOver(g2d);
        }
    }

    /**
     * Draw the player spaceship (triangle pointing upward)
     */
    private void drawPlayer(Graphics2D g) {
        int x = model.getPlayerX();
        int y = model.getPlayerY();
        int size = model.getPlayerSize();

        g.setColor(Color.GREEN);

        // Draw a simple triangle pointing up
        int[] xPoints = {x + size / 2, x, x + size};
        int[] yPoints = {y, y + size, y + size};
        g.fillPolygon(xPoints, yPoints, 3);

        // Draw outline
        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(xPoints, yPoints, 3);
    }

    /**
     * Draw all aliens in the formation
     */
    private void drawAliens(Graphics2D g) {
        boolean[][] alienGrid = model.getAlienGrid();
        int alienSize = model.getAlienSize();
        int formationX = model.getAlienFormationX();
        int formationY = model.getAlienFormationY();

        g.setColor(Color.RED);

        for (int row = 0; row < GameModel.getAlienRows(); row++) {
            for (int col = 0; col < GameModel.getAlienCols(); col++) {
                if (alienGrid[row][col]) {
                    int alienX = formationX + col * alienSize;
                    int alienY = formationY + row * alienSize;

                    // Draw alien as a simple square with eyes
                    g.fillRect(alienX, alienY, alienSize, alienSize);

                    // Draw eyes
                    g.setColor(Color.BLACK);
                    g.fillRect(alienX + 5, alienY + 5, 3, 3);
                    g.fillRect(alienX + alienSize - 8, alienY + 5, 3, 3);

                    g.setColor(Color.RED);
                }
            }
        }
    }

    /**
     * Draw all bullets (both player and alien)
     */
    private void drawBullets(Graphics2D g) {
        // Draw player bullet
        GameModel.PlayerBullet playerBullet = model.getPlayerBullet();
        if (playerBullet != null && playerBullet.active) {
            g.setColor(Color.YELLOW);
            g.fillRect(playerBullet.x, playerBullet.y, 4, 10);
        }

        // Draw alien bullets
        for (GameModel.AlienBullet bullet : model.getAlienBullets()) {
            if (bullet.active) {
                g.setColor(Color.MAGENTA);
                g.fillRect(bullet.x, bullet.y, 4, 8);
            }
        }
    }

    /**
     * Draw the heads-up display (score and lives)
     */
    private void drawHUD(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Draw score
        String scoreText = "Score: " + model.getScore();
        g.drawString(scoreText, 10, 20);

        // Draw lives
        String livesText = "Lives: " + model.getLives();
        g.drawString(livesText, model.getGameWidth() - 150, 20);
    }

    /**
     * Draw centered game-over message
     */
    private void drawGameOver(Graphics2D g) {
        int width = model.getGameWidth();
        int height = model.getGameHeight();

        // Semi-transparent dark overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width, height);

        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String gameOverText = "GAME OVER";
        FontMetrics metrics = g.getFontMetrics();
        int textX = (width - metrics.stringWidth(gameOverText)) / 2;
        int textY = (height / 2) - 30;
        g.drawString(gameOverText, textX, textY);

        // Final score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String finalScoreText = "Final Score: " + model.getScore();
        metrics = g.getFontMetrics();
        textX = (width - metrics.stringWidth(finalScoreText)) / 2;
        g.drawString(finalScoreText, textX, textY + 60);

        // Aliens killed
        String aliensKilledText = "Aliens Defeated: " + model.getAlienKillCount();
        metrics = g.getFontMetrics();
        textX = (width - metrics.stringWidth(aliensKilledText)) / 2;
        g.drawString(aliensKilledText, textX, textY + 100);
    }

    /**
     * Update the display by requesting a repaint
     */
    public void updateDisplay() {
        repaint();
    }
}
