/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

/**
 *
 * @author SATYA NAVEEN
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class ASCStockItem {
    private String productCode; // Format ABC1234567, must be unique
    private String title; // Text 120 characters maximum
    private String description; // Text 500 characters maximum
    private int unitPricePounds; // Unit-price in pounds
    private int unitPricePence; // Unit-price in pence
    private int quantityInStock; // Quantity in stock
    
    // List of observers interested in low stock notifications
    private List<LowStockObserver> lowStockObservers = new ArrayList<>();
    // Regex pattern for product code validation
    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile("^[A-Z]{3}\\d{7}$");
    
    public ASCStockItem(String productCode, String title, String description,
                        int unitPricePounds, int unitPricePence, int quantityInStock) {
        setProductCode(productCode);
        setTitle(title);
        setDescription(description);
        setUnitPricePounds(unitPricePounds);
        setUnitPricePence(unitPricePence);
        setQuantityInStock(quantityInStock);
    }

    // Getters and Setters with validation
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String Code) {
        if (!PRODUCT_CODE_PATTERN.matcher(Code).matches()) {
            throw new IllegalArgumentException("Invalid product code format.");
        }
        this.productCode = Code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.length() > 120) {
            throw new IllegalArgumentException("The title must not be null and must be 120 characters or less.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        if (desc == null || desc.length() > 500) {
            throw new IllegalArgumentException("Description must not be null and must be 500 characters or less.");
        }
        this.description = desc;
    }

    public int getUnitPricePounds() {
        return unitPricePounds;
    }

    public void setUnitPricePounds(int unitPricePounds) {
        if (unitPricePounds < 0) {
            throw new IllegalArgumentException("price in pounds must be non-negative.");
        }
        this.unitPricePounds = unitPricePounds;
    }

    public int getUnitPricePence() {
        return unitPricePence;
    }

    public void setUnitPricePence(int unitPricePence) {
        if (unitPricePence < 0 || unitPricePence >= 100) {
            throw new IllegalArgumentException("price in pence must be between 0 and 99.");
        }
        this.unitPricePence = unitPricePence;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity in stock must be non-negative.");
        }
        this.quantityInStock = quantity;
    }

    // Override toString() method for displaying in GUI
    @Override
    public String toString() {
        return String.format("Code: %s - Title: %s - Desc: %s - Unit Price: £%d.%02d - Qty: %d",
                productCode, title, description, unitPricePounds, unitPricePence, quantityInStock);
    }
    
    // Business Logic Methods
    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative.");
        }
        this.quantityInStock += quantity;
    }

    public void sellStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to sell must be non-negative.");
        }
        if (this.quantityInStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock available.");
        }
        this.quantityInStock -= quantity;
        notifyLowStockObservers();
    }
    
    // Method to attach observers
    public void attachLowStockObserver(LowStockObserver observer) {
        lowStockObservers.add(observer);
    }

    // Method to notify observers of low stock
    private void notifyLowStockObservers() {
        if (this.quantityInStock < 5) { // Threshold for low stock
            for (LowStockObserver observer : lowStockObservers) {
                observer.notifyLowStock(this);
            }
        }
    }
    
    // Method to save stock items to a CSV file
    public static void saveStockItems(String csvFilePath, List<ASCStockItem> stockItems) throws IOException {
        List<String> lines = new ArrayList<>();

        for (ASCStockItem item : stockItems) {
            lines.add(String.join(",",
                    item.getProductCode(),
                    item.getTitle(),
                    item.getDescription(),
                    String.valueOf(item.getUnitPricePounds()),
                    String.valueOf(item.getUnitPricePence()),
                    String.valueOf(item.getQuantityInStock())));
        }

        Files.write(Paths.get(csvFilePath), lines);
    }
    
    
}


