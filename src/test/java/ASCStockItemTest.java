/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import existing.ASCStockItem;
import existing.LowStockObserver;

public class ASCStockItemTest {

    private ASCStockItem validItem;

    @BeforeEach
    public void setUp() {
        //Initialize a valid ASCStockItem object
        validItem = new ASCStockItem("RUN1234567", "Running Shoes", "Comfortable running shoes", 50, 99, 10);
    }

    @Test
    public void testValidConstructor() {
        assertEquals("RUN1234567", validItem.getProductCode());
        assertEquals("Running Shoes", validItem.getTitle());
        assertEquals("Comfortable running shoes", validItem.getDescription());
        assertEquals(50, validItem.getUnitPricePounds());
        assertEquals(99, validItem.getUnitPricePence());
        assertEquals(10, validItem.getQuantityInStock());
    }

    @Test
    public void testInvalidProductCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ASCStockItem("INVALID", "Invalid Item", "Invalid product code", 10, 0, 5);
        });
    }

    @Test
    public void testAddStock() {
        validItem.addStock(5);
        assertEquals(15, validItem.getQuantityInStock());
    }

    @Test
    public void testAddNegativeStock() {
        assertThrows(IllegalArgumentException.class, () -> {
            validItem.addStock(-5);
        });
    }

    @Test
    public void testSellStock() {
        validItem.sellStock(5);
        assertEquals(5, validItem.getQuantityInStock());
    }

    @Test
    public void testSellStockMoreThanAvailable() {
        assertThrows(IllegalArgumentException.class, () -> {
            validItem.sellStock(15);
        });
    }

    @Test
    void testSetProductCodeWithInvalidPattern() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setProductCode("123"));
    }

    @Test
    void testSetTitleWithNull() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setTitle(null));
    }

    @Test
    void testSetTitleWithLongString() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setTitle("a".repeat(121)));
    }

    @Test
    void testSetDescriptionWithLongString() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setDescription("a".repeat(501)));
    }

    @Test
    void testSetNegativeUnitPricePounds() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setUnitPricePounds(-1));
    }

    @Test
    void testSetInvalidUnitPricePence() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setUnitPricePence(100));
    }

    @Test
    void testSetNegativeQuantityInStock() {
        assertThrows(IllegalArgumentException.class, () -> validItem.setQuantityInStock(-1));
    }

    @Test
    void testSellStockExactAmount() {
        validItem.sellStock(10);
        assertEquals(0, validItem.getQuantityInStock());
    }

    @Test
    void testLowStockNotification() {
        MockLowStockObserver mockObserver = new MockLowStockObserver();
        validItem.attachLowStockObserver(mockObserver);
        validItem.setQuantityInStock(5); // Set to the threshold
        validItem.sellStock(1); // This should trigger low stock notification
        assertTrue(mockObserver.isNotified());
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
