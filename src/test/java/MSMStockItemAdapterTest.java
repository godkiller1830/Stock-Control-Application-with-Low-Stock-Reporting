/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import existing.MSMStockItem;
import existing.MSMStockItemAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MSMStockItemAdapterTest {

    private MSMStockItem msmItem;
    private MSMStockItemAdapter adapter;

    @BeforeEach
    void setUp() {
        // Initialize a MSMStockItem and its adapter
        msmItem = new MSMStockItem(1, "123456", "Running Shoes                                               Comfortable running shoes", 5099, 10);
        adapter = new MSMStockItemAdapter(msmItem);
    }

    @Test
    void testConstructorAndProductCode() {
        // Test the adapter's constructor and product code generation
        String expectedCode = "RUN0123456"; // Assuming departmentId 1 maps to "RUN"
        assertEquals(expectedCode, adapter.getProductCode());
    }

    @Test
    void testMethodDelegation() {
        // Test that the adapter correctly delegates to the MSMStockItem
        assertEquals(msmItem.getName(), adapter.getTitle());
        assertEquals(msmItem.getDescription(), adapter.getDescription());
        assertEquals(msmItem.getUnitPrice() / 100, adapter.getUnitPricePounds());
        assertEquals(msmItem.getUnitPrice() % 100, adapter.getUnitPricePence());
        assertEquals(msmItem.getQuantityInStock(), adapter.getQuantityInStock());
    }

    @Test
    void testSellStock() {
        // Assuming sellStock is overridden to also update the MSMStockItem's quantity
        int quantityToSell = 5;
        adapter.sellStock(quantityToSell);
        assertEquals(5, adapter.getQuantityInStock());
        assertEquals(5, msmItem.getQuantityInStock()); // Confirm MSMStockItem is also updated
    }

    @Test
    void testSellStockMoreThanAvailable() {
        // Test selling more stock than is available
        assertThrows(IllegalArgumentException.class, () -> adapter.sellStock(15));
        // Ensure the stock level hasn't changed
        assertEquals(10, adapter.getQuantityInStock());
    }

    @Test
void testAddStock() {
    // Test adding stock to the adapter
    int quantityToAdd = 5;
    adapter.addStock(quantityToAdd);
    
    int expectedQuantity = 10 + quantityToAdd; // 10 is the initial quantity in setUp
    assertEquals(expectedQuantity, adapter.getQuantityInStock(), "Adapter quantity should be updated.");

    // Confirm the MSMStockItem is also updated
    assertEquals(expectedQuantity, msmItem.getQuantityInStock(), "Wrapped MSMStockItem quantity should be updated.");
}

    @Test
    void testSetNegativeQuantity() {
        //setQuantity method in MSMStockItem prevents negative values
        assertThrows(IllegalArgumentException.class, () -> adapter.setQuantityInStock(-5));
        // Ensure the stock level hasn't changed
        assertEquals(10, adapter.getQuantityInStock());
    }

    
}
