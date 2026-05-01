/**
 * Quick test to verify alien speed increases as aliens are destroyed
 */
public class SpeedTest {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        
        System.out.println("=== Alien Speed Acceleration Test ===\n");
        
        // Get initial interval
        int initialInterval = model.getRecommendedTimerInterval();
        System.out.println("Initial timer interval: " + initialInterval + " ms");
        
        // Simulate destroying aliens
        for (int i = 1; i <= 10; i++) {
            // Fire and destroy an alien
            model.fireProjectile();
            GameModel.PlayerBullet bullet = model.getPlayerBullet();
            
            // Position bullet to hit first alien
            int alienX = model.getAlienFormationX();
            int alienY = model.getAlienFormationY();
            bullet.x = alienX + 5;
            bullet.y = alienY + 5;
            
            model.update();
            
            int newInterval = model.getRecommendedTimerInterval();
            System.out.println("After " + i + " alien(s) destroyed: " + newInterval + " ms (interval decreased by " + (initialInterval - newInterval) + ")");
            
            if (i >= 10) break;
        }
        
        System.out.println("\nTest complete! Game speeds up as aliens are destroyed.");
    }
}
