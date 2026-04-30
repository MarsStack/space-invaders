/**
 * ModelTester.java
 *
 * Simple test suite for GameModel without testing libraries.
 * Tests core game logic and state management.
 * Each test prints PASS or FAIL with a description.
 */
public class ModelTester {

    private static int testCount = 0;
    private static int passCount = 0;

    /**
     * Print a test result
     */
    private static void printResult(String testName, boolean passed) {
        testCount++;
        String result = passed ? "PASS" : "FAIL";
        System.out.println("[" + result + "] " + testName);
        if (passed) {
            passCount++;
        }
    }

    /**
     * Test 1: Player cannot move past left edge
     */
    private static void testPlayerLeftBoundary() {
        GameModel model = new GameModel();
        int initialX = model.getPlayerX();

        // Move left many times
        for (int i = 0; i < 100; i++) {
            model.movePlayerLeft();
        }

        boolean passed = model.getPlayerX() >= 0;
        printResult("Player cannot move past left edge", passed);
    }

    /**
     * Test 2: Player cannot move past right edge
     */
    private static void testPlayerRightBoundary() {
        GameModel model = new GameModel();

        // Move right many times
        for (int i = 0; i < 100; i++) {
            model.movePlayerRight();
        }

        int maxX = model.getPlayerX() + model.getPlayerSize();
        boolean passed = maxX <= model.getGameWidth();
        printResult("Player cannot move past right edge", passed);
    }

    /**
     * Test 3: Firing while a bullet is already in flight does nothing
     */
    private static void testBulletInFlightLimit() {
        GameModel model = new GameModel();

        // Fire first bullet
        model.fireProjectile();
        GameModel.PlayerBullet firstBullet = model.getPlayerBullet();
        int firstX = firstBullet.x;
        int firstY = firstBullet.y;

        // Try to fire again
        model.fireProjectile();
        GameModel.PlayerBullet secondBullet = model.getPlayerBullet();

        // Should be the same bullet object
        boolean passed = (firstBullet == secondBullet) && (firstX == secondBullet.x) && (firstY == secondBullet.y);
        printResult("Cannot fire while bullet already in flight", passed);
    }

    /**
     * Test 4: A bullet that reaches the top is removed
     */
    private static void testBulletRemovalAtTop() {
        GameModel model = new GameModel();

        // Fire a bullet
        model.fireProjectile();
        GameModel.PlayerBullet bullet = model.getPlayerBullet();

        // Update many times to move bullet off screen
        for (int i = 0; i < 200; i++) {
            model.update();
        }

        boolean passed = !bullet.active;
        printResult("Bullet is removed when reaching top", passed);
    }

    /**
     * Test 5: Destroying an alien increases the score
     */
    private static void testScoreIncreaseOnAlienDestruction() {
        GameModel model = new GameModel();

        int initialScore = model.getScore();

        // Get an alien from the first row
        boolean[][] alienGrid = model.getAlienGrid();
        if (alienGrid[0][0]) {
            // Fire a bullet
            model.fireProjectile();
            GameModel.PlayerBullet bullet = model.getPlayerBullet();

            // Manually position bullet to collide with first alien
            int alienX = model.getAlienFormationX();
            int alienY = model.getAlienFormationY();
            bullet.x = alienX + 5;
            bullet.y = alienY + 5;

            // Call update to check collisions
            model.update();

            // Check if score increased
            int newScore = model.getScore();
            boolean passed = (newScore == initialScore + 10) && !alienGrid[0][0];
            printResult("Score increases by 10 when alien is destroyed", passed);
        } else {
            printResult("Score increases by 10 when alien is destroyed", false);
        }
    }

    /**
     * Test 6: Losing all lives triggers game-over state
     */
    private static void testGameOverOnAllLivesLost() {
        GameModel model = new GameModel();

        // Verify game starts with 3 lives
        if (model.getLives() != 3) {
            printResult("Game over triggers when all lives lost", false);
            return;
        }

        // Manually create alien bullets that will hit the player
        int playerX = model.getPlayerX();
        int playerY = model.getPlayerY();

        // Lose all 3 lives by creating bullets at player position
        for (int life = 0; life < 3; life++) {
            // Create a bullet directly using reflection or by manipulating the list
            // We'll use the public getAlienBullets() list
            java.util.List<GameModel.AlienBullet> bullets = model.getAlienBullets();
            GameModel.AlienBullet bullet = new GameModel.AlienBullet(playerX + 5, playerY + 5);
            bullet.active = true;
            bullets.add(bullet);

            // Update to check collision
            model.update();
        }

        // After 3 collisions, game should be over
        boolean passed = !model.isGameRunning() && model.getLives() <= 0;
        printResult("Game over triggers when all lives lost", passed);
    }

    /**
     * Test 7: Initial game state is correct
     */
    private static void testInitialGameState() {
        GameModel model = new GameModel();

        boolean scoreMissing = model.getScore() == 0;
        boolean livesCorrect = model.getLives() == 3;
        boolean gameRunning = model.isGameRunning();
        boolean notPaused = !model.isGamePaused();

        boolean passed = scoreMissing && livesCorrect && gameRunning && notPaused;
        printResult("Initial game state is correct", passed);
    }

    /**
     * Test 8: Alien formation has correct dimensions
     */
    private static void testAlienFormationDimensions() {
        GameModel model = new GameModel();

        boolean[][] alienGrid = model.getAlienGrid();
        boolean correctRows = alienGrid.length == GameModel.getAlienRows();
        boolean correctCols = alienGrid[0].length == GameModel.getAlienCols();
        boolean allActive = true;

        // Check all aliens are initially alive
        for (int row = 0; row < GameModel.getAlienRows(); row++) {
            for (int col = 0; col < GameModel.getAlienCols(); col++) {
                if (!alienGrid[row][col]) {
                    allActive = false;
                }
            }
        }

        boolean passed = correctRows && correctCols && allActive;
        printResult("Alien formation has correct dimensions (5x11)", passed);
    }

    /**
     * Main method - run all tests
     */
    public static void main(String[] args) {
        System.out.println("=== Space Invaders Model Tester ===\n");

        testPlayerLeftBoundary();
        testPlayerRightBoundary();
        testBulletInFlightLimit();
        testBulletRemovalAtTop();
        testScoreIncreaseOnAlienDestruction();
        testGameOverOnAllLivesLost();
        testInitialGameState();
        testAlienFormationDimensions();

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passCount + "/" + testCount);
        if (passCount == testCount) {
            System.out.println("All tests passed!");
        } else {
            System.out.println("Some tests failed.");
        }
    }
}
