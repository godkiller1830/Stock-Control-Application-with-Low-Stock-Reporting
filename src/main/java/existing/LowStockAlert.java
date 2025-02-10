/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

/**
 *
 * @author SATYA NAVEEN
 */
import javax.swing.JOptionPane;

public class LowStockAlert implements LowStockObserver {
    private boolean isConsoleApp;

    public LowStockAlert(boolean isConsoleApp) {
        this.isConsoleApp = isConsoleApp;
    }

    @Override
    public void notifyLowStock(ASCStockItem item) {
        if (isConsoleApp) {
            // Console-based notification
            System.out.println("Warning - Low Stock Alert for " + item.getTitle() + " (" + item.getProductCode() + ")");
        } else {
            // GUI-based notification
            JOptionPane.showMessageDialog(null, 
                "Warning: Low stock alert for " + item.getTitle() + "\nProduct Code: " 
                + item.getProductCode() + "\nQuantity left: " + item.getQuantityInStock(),
                "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }
}


