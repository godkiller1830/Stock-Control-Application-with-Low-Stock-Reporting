/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

/**
 *
 * @author SATYA NAVEEN
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class StockItemUtils {
    public static List<ASCStockItem> loadASCStockItemsFromCSV(String csvFilePath) {
        List<ASCStockItem> stockItems = new ArrayList<>();
        // SAme logic to load ASCStockItems from CSV
        String[] headers = {"Code", "Title", "Description", "Unit Price (Pounds)", "Unit Price (Pence)", "Quantity"};

        try (Reader reader = new FileReader(csvFilePath)) {
            // Configure CSVFormat to parse without a header and set the headers manually
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withHeader(headers)
                    .withSkipHeaderRecord(false);

            CSVParser csvParser = new CSVParser(reader, csvFormat);

            // Iterate over the records and add them to the table model
            for (CSVRecord csvRecord : csvParser) {
                // Create an ASCStockItem object from each record
                String code = csvRecord.get("Code");
                String title = csvRecord.get("Title");
                String description = csvRecord.get("Description");
                int unitPricePounds = Integer.parseInt(csvRecord.get("Unit Price (Pounds)"));
                int unitPricePence = Integer.parseInt(csvRecord.get("Unit Price (Pence)"));
                int quantity = Integer.parseInt(csvRecord.get("Quantity"));

                ASCStockItem item = new ASCStockItem(code, title, description, unitPricePounds, unitPricePence, quantity);

                // Attach the observer
                //item.attachLowStockObserver(lowStockAlert);
                stockItems.add(item);

            }
        }
            catch (IOException e) {
            e.printStackTrace();
        }
        return stockItems;
    }
    public static List<MSMStockItem> loadMSMStockItemsFromCSV(String csvFilePath) {
        final List<MSMStockItem> loadedStock = new ArrayList<>();

        //------------------------------------------------------------------
        // TODO: Add code to load CSV file.
        String line = "";
        // csvFilePath = "C:/Users/SATYA NAVEEN/Downloads/MengdasSportyMart.csv"; // Replace with your CSV file path
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                String[] itemData = line.split(",");

                if (itemData.length >= 5) { // Ensure there are enough data fields
                    int departmentId = Integer.parseInt(itemData[0].trim());
                    String productCode = itemData[1].trim();
                    String nameDescription = itemData[2]; // Name and description as a combined field
                    int unitPricePence = Integer.parseInt(itemData[3].trim());
                    int quantityInStock = Integer.parseInt(itemData[4].trim());

                    // Create MSMStockItem using the constructor that expects the combined name and description field
                    MSMStockItem stockItem = new MSMStockItem(departmentId, productCode, nameDescription, unitPricePence, quantityInStock);
                    loadedStock.add(stockItem);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("CSV File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading CSV File: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing numeric data: " + e.getMessage());
            System.out.println("Skipping line: " + line);
            
        }
        //------------------------------------------------------------------

        return loadedStock;
    }
}
