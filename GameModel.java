import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameModel.java
 *
 * Model class for Space Invaders game.
 * Handles all game logic, state management, and entity positions.
 * Contains no Swing imports - purely business logic.
 *
 * Responsibilities:
 * - Track player position and state
 * - Track enemy positions and movement
 * - Track projectiles and collisions
 * - Manage game state (running, paused, game over)
 * - Update game entities based on game logic
 * - Calculate collisions between entities
 */
public class GameModel {

    // Game constants
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int PLAYER_SIZE = 40;
    private static final int PLAYER_SPEED = 5;
    private static final int PLAYER_Y = GAME_HEIGHT - 50;

    private static final int ALIEN_SIZE = 30;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLS = 11;
    private static final int ALIEN_SPEED = 1;
    private static final int ALIEN_DROP = 15;

    private static final int BULLET_SPEED = 7;
    private static final int ALIEN_BULLET_SPEED = 4;

    // Player data
    private int playerX;
    private int playerY;

    // Alien formation (5 rows x 11 columns)
    private boolean[][] alienGrid;
    private int alienFormationX;
    private int alienFormationY;
    private int alienDirection; // 1 = right, -1 = left

    // Bullets
    private PlayerBullet playerBullet;
    private List<AlienBullet> alienBullets;
    private int alienFireCounter;
    private static final int ALIEN_FIRE_RATE = 120; // Fire every ~2 seconds at 60 FPS

    // Game state
    private boolean isRunning;
    private boolean isPaused;
    private int score;
    private int lives;
    private int alienKillCount;

    // Random for alien firing
    private Random random;

    /**
     * Inner class to represent a player bullet
     */
    public static class PlayerBullet {
        public int x, y;
        public boolean active;

        public PlayerBullet(int x, int y) {
            this.x = x;
            this.y = y;
            this.active = true;
        }
    }

    /**
     * Inner class to represent an alien bullet
     */
    public static class AlienBullet {
        public int x, y;
        public boolean active;

        public AlienBullet(int x, int y) {
            this.x = x;
            this.y = y;
            this.active = true;
        }
    }

    /**
     * Constructor - initializes the game model
     */
    public GameModel() {
        this.isRunning = true;
        this.isPaused = false;
        this.score = 0;
        this.lives = 3;
        this.alienKillCount = 0;
        this.random = new Random();

        // Initialize player at bottom center
        this.playerX = GAME_WIDTH / 2 - PLAYER_SIZE / 2;
        this.playerY = PLAYER_Y;

        // Initialize alien formation
        this.alienGrid = new boolean[ALIEN_ROWS][ALIEN_COLS];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                alienGrid[row][col] = true;
            }
        }
        this.alienFormationX = 50;
        this.alienFormationY = 50;
        this.alienDirection = 1; // Start moving right

        // Initialize bullets
        this.playerBullet = null;
        this.alienBullets = new ArrayList<>();
        this.alienFireCounter = 0;
    }

    /**
     * Update game state - called each frame
     */
    public void update() {
        if (isPaused || !isRunning) {
            return;
        }

        // Update player bullet
        if (playerBullet != null && playerBullet.active) {
            playerBullet.y -= BULLET_SPEED;
            if (playerBullet.y < 0) {
                playerBullet.active = false;
            }
        }

        // Update alien bullets
        for (AlienBullet bullet : alienBullets) {
            if (bullet.active) {
                bullet.y += ALIEN_BULLET_SPEED;
                if (bullet.y > GAME_HEIGHT) {
                    bullet.active = false;
                }
            }
        }

        // Move alien formation
        moveAlienFormation();

        // Fire alien bullets randomly
        alienFireCounter++;
        if (alienFireCounter >= ALIEN_FIRE_RATE) {
            fireAlienBullet();
            alienFireCounter = 0;
        }

        // Check collisions
        checkCollisions();

        // Check if player lost a life
        for (AlienBullet bullet : alienBullets) {
            if (bullet.active && isColliding(bullet.x, bullet.y, 5, playerX, playerY, PLAYER_SIZE)) {
                lives--;
                bullet.active = false;
                if (lives <= 0) {
                    isRunning = false;
                }
            }
        }

        // Check if aliens reached the bottom
        int bottomMostAlien = alienFormationY + (ALIEN_ROWS - 1) * ALIEN_SIZE;
        if (bottomMostAlien >= PLAYER_Y) {
            isRunning = false;
        }
    }

    /**
     * Move the alien formation right/left and down when hitting edges
     */
    private void moveAlienFormation() {
        // Check if we should change direction
        int leftBound = 0;
        int rightBound = GAME_WIDTH - (ALIEN_COLS * ALIEN_SIZE);

        if ((alienDirection == 1 && alienFormationX >= rightBound) ||
            (alienDirection == -1 && alienFormationX <= leftBound)) {
            // Hit the edge - move down and reverse direction
            alienFormationY += ALIEN_DROP;
            alienDirection *= -1;
        }

        // Move horizontally
        alienFormationX += alienDirection * ALIEN_SPEED;
    }

    /**
     * Fire a bullet from a random alien
     */
    private void fireAlienBullet() {
        // Find a random active alien to fire from
        List<int[]> activeAliens = new ArrayList<>();
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                if (alienGrid[row][col]) {
                    activeAliens.add(new int[]{row, col});
                }
            }
        }

        if (!activeAliens.isEmpty()) {
            int[] alien = activeAliens.get(random.nextInt(activeAliens.size()));
            int alienScreenX = alienFormationX + alien[1] * ALIEN_SIZE + ALIEN_SIZE / 2;
            int alienScreenY = alienFormationY + alien[0] * ALIEN_SIZE + ALIEN_SIZE;
            alienBullets.add(new AlienBullet(alienScreenX, alienScreenY));
        }
    }

    /**
     * Check collisions between player bullet and aliens
     */
    private void checkCollisions() {
        if (playerBullet == null || !playerBullet.active) {
            return;
        }

        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                if (alienGrid[row][col]) {
                    int alienScreenX = alienFormationX + col * ALIEN_SIZE;
                    int alienScreenY = alienFormationY + row * ALIEN_SIZE;

                    if (isColliding(playerBullet.x, playerBullet.y, 5,
                                  alienScreenX, alienScreenY, ALIEN_SIZE)) {
                        alienGrid[row][col] = false;
                        playerBullet.active = false;
                        score += 10;
                        alienKillCount++;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Check if two rectangular objects are colliding
     */
    private boolean isColliding(int x1, int y1, int w1, int x2, int y2, int w2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + w2 && y1 + w1 > y2;
    }

    /**
     * Handle player movement
     */
    public void movePlayerLeft() {
        if (playerX > 0) {
            playerX -= PLAYER_SPEED;
        }
    }

    public void movePlayerRight() {
        if (playerX < GAME_WIDTH - PLAYER_SIZE) {
            playerX += PLAYER_SPEED;
        }
    }

    public void fireProjectile() {
        if (playerBullet == null || !playerBullet.active) {
            playerBullet = new PlayerBullet(playerX + PLAYER_SIZE / 2 - 2, playerY);
        }
    }

    /**
     * Game state methods
     */
    public void pause() {
        this.isPaused = !this.isPaused;
    }

    public boolean isGameRunning() {
        return isRunning;
    }

    public boolean isGamePaused() {
        return isPaused;
    }

    public int getGameWidth() {
        return GAME_WIDTH;
    }

    public int getGameHeight() {
        return GAME_HEIGHT;
    }

    // Getters for view rendering
    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getPlayerSize() {
        return PLAYER_SIZE;
    }

    public int getAlienFormationX() {
        return alienFormationX;
    }

    public int getAlienFormationY() {
        return alienFormationY;
    }

    public boolean[][] getAlienGrid() {
        return alienGrid;
    }

    public int getAlienSize() {
        return ALIEN_SIZE;
    }

    public PlayerBullet getPlayerBullet() {
        return playerBullet;
    }

    public List<AlienBullet> getAlienBullets() {
        return alienBullets;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getAlienKillCount() {
        return alienKillCount;
    }

    public static int getAlienRows() {
        return ALIEN_ROWS;
    }

    public static int getAlienCols() {
        return ALIEN_COLS;
    }
}
