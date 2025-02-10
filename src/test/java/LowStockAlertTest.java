/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import existing.ASCStockItem;
import existing.LowStockAlert;
import existing.LowStockObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class LowStockAlertTest {
    
    private ASCStockItem mockItem;
    private LowStockAlert consoleAlert;
    private LowStockAlert guiAlert;

    @BeforeEach
    void setUp() {
        // Initialize common objects used in the tests
        mockItem = new ASCStockItem("RUN1234567", "Running Shoes", "Comfortable running shoes", 50, 99, 4);
        consoleAlert = new LowStockAlert(true);
        guiAlert = new LowStockAlert(false);
    }
    @Test
    public void testConsoleNotification() {
        // Arrange
        ASCStockItem mockItem = new ASCStockItem("RUN1234567", "Running Shoes", "Comfortable running shoes", 50, 99, 4);
        LowStockAlert consoleAlert = new LowStockAlert(true);
        
        // Act and Assert
        // Ideally, you'd capture console output and assert it contains the expected message.
        // For simplicity, just ensure it doesn't throw an exception.
        assertDoesNotThrow(() -> consoleAlert.notifyLowStock(mockItem));
    }

    @Test
    public void testGUINotification() {
        // Arrange
        ASCStockItem mockItem = new ASCStockItem("RUN1234567", "Running Shoes", "Comfortable running shoes", 50, 99, 4);
        LowStockAlert guiAlert = new LowStockAlert(false);
        
        // Act and Assert
        // It's challenging to test GUI elements in a unit test. 
        // For simplicity, just ensure it doesn't throw an exception.
        assertDoesNotThrow(() -> guiAlert.notifyLowStock(mockItem));
    }
    
    // You might want more tests here to cover various edge cases and scenarios.
    @Test
    public void testNotificationAtDifferentStockLevels() {
        // Test just above the threshold
        mockItem.setQuantityInStock(6);
        assertDoesNotThrow(() -> consoleAlert.notifyLowStock(mockItem));

        // Test at the threshold
        mockItem.setQuantityInStock(5);
        assertDoesNotThrow(() -> consoleAlert.notifyLowStock(mockItem));

        // Test just below the threshold
        mockItem.setQuantityInStock(4);
        assertDoesNotThrow(() -> consoleAlert.notifyLowStock(mockItem));
    }

    @Test
    public void testMultipleObservers() {
        // Attach multiple observers and ensure they all get notified
        MockLowStockObserver observer1 = new MockLowStockObserver();
        MockLowStockObserver observer2 = new MockLowStockObserver();
        mockItem.attachLowStockObserver(observer1);
        mockItem.attachLowStockObserver(observer2);

        // Reduce stock to trigger notification
        mockItem.sellStock(1);

        assertTrue(observer1.isNotified(), "Observer 1 should be notified");
        assertTrue(observer2.isNotified(), "Observer 2 should be notified");
    }


    // Inner class to mock the observer for testing
    private static class MockLowStockObserver implements LowStockObserver {
        private boolean notified = false;

        @Override
        public void notifyLowStock(ASCStockItem item) {
            notified = true;
        }

        public boolean isNotified() {
            return notified;
        }
    }
}
