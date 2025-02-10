/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author SATYA NAVEEN
 */
public class ConsoleApplication {
    public static void main(String[] args) {
    runConsoleApplication();
    }
    
    private static void runConsoleApplication() {
        System.out.println("Running in console mode...");

        // Load stock items from CSV files
        List<ASCStockItem> ascStockItems = StockItemUtils.loadASCStockItemsFromCSV("AshersSportsCollective.csv");
        List<MSMStockItem> msmStockItems = StockItemUtils.loadMSMStockItemsFromCSV("MengdasSportyMart.csv");

        // Instantiate LowStockAlert for console
        LowStockAlert lowStockAlert = new LowStockAlert(true);

        // Attach the low stock observer to all items
        for (ASCStockItem item : ascStockItems) {
            item.attachLowStockObserver(lowStockAlert);
        }

        // Console interaction
        Scanner scanner = new Scanner(System.in);
        boolean looping = true;
        while (looping) {
            System.out.println("\nEnter your command  from the list(list, buy, sell, quit):");
            String command = scanner.nextLine().trim();

            switch (command.toLowerCase()) {
                case "list":
                    listStockItems(ascStockItems, msmStockItems);
                    break;
                case "buy":
                    handleBuy(scanner, ascStockItems, msmStockItems);
                    break;
                case "sell":
                    handleSell(scanner, ascStockItems, msmStockItems);
                    break;
                case "quit":
                    looping = false;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
                    break;
            }
        }
        scanner.close();
    }

    private static void listStockItems(List<ASCStockItem> ascStockItems, List<MSMStockItem> msmStockItems) {
        for (ASCStockItem item : ascStockItems) {
            System.out.println(item);
        }
        for (MSMStockItem item : msmStockItems) {
            System.out.println(formatMSMStockItem(item));
        }
    }

    private static String formatMSMStockItem(MSMStockItem item) {
        String map = idToNameConverter(item.getDepartmentId());
        String department = map + item.getCode();
        String name = item.getName().trim();
        String description = item.getDescription().trim();
        String unitPrice = item.getHumanFriendlyUnitPrice();
        int quantity = item.getQuantityInStock();

        return String.format("code: %s - title: %s - desc: %s - UNIT PRICE: £%s - QTY: %d",
                department, name, description, unitPrice, quantity);
    }

    private static String idToNameConverter(int departmentId) {
        switch (departmentId) {
            case 1:
                return "RUN";
            case 2:
                return "SWM";
            case 3:
                return "CYC";
            default:
                return "OTH"; // Other/Unknown
        }
    }

    private static void handleBuy(Scanner scanner, List<ASCStockItem> ascStockItems, List<MSMStockItem> msmStockItems) {
        System.out.println("Enter product code:");
        String code = scanner.nextLine();
        System.out.println("Enter quantity to buy:");
        int quantity = Integer.parseInt(scanner.nextLine());

        ASCStockItem item = findStockItemByCode(ascStockItems, msmStockItems, code);
        if (item != null) {
            item.addStock(quantity);
            System.out.println("Added " + quantity + " units to stock for " + item.getTitle());
        } else {
            System.out.println("Stock item not found.");
        }
    }

    private static void handleSell(Scanner scanner, List<ASCStockItem> ascStockItems, List<MSMStockItem> msmStockItems) {
        System.out.println("Enter product code:");
        String code = scanner.nextLine();
        System.out.println("Enter quantity to sell:");
        int quantity = Integer.parseInt(scanner.nextLine());

        ASCStockItem item = findStockItemByCode(ascStockItems, msmStockItems, code);
        if (item != null) {
            if (item.getQuantityInStock() >= quantity) {
                item.sellStock(quantity);
                System.out.println("Sold " + quantity + " units of " + item.getTitle());
            } else {
                System.out.println("Not enough stock to sell.");
            }
        } else {
            System.out.println("Stock item not found.");
        }
    }

    private static ASCStockItem findStockItemByCode(List<ASCStockItem> ascStockItems, List<MSMStockItem> msmStockItems, String code) {
        for (ASCStockItem item : ascStockItems) {
            if (item.getProductCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        for (MSMStockItem msmItem : msmStockItems) {
            if (msmItem.getCode().equalsIgnoreCase(code)) {
                // If found, wrap the MSMStockItem in an adapter and return it
                return new MSMStockItemAdapter(msmItem);
            }
        }
        return null;
    }
}
