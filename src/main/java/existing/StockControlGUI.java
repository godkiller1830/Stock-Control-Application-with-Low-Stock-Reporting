/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

/**
 *
 * @author SATYA NAVEEN
 */
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.io.FileNotFoundException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.Scanner;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * The StockControlGUI class is responsible for creating the graphical user
 * interface for the stock control application. It handles user interactions for
 * buying and selling stock items, as well as displaying low stock alerts and
 * sales transactions.
 */
public class StockControlGUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton buyButton;
    private JButton sellButton;
    private JLabel messageLabel;
    private LowStockAlert lowStockAlert;
    private JButton viewSalesButton;

    /**
     * Constructor for the StockControlGUI class. It initializes the UI
     * components and sets up event listeners for user actions.
     */
    public StockControlGUI() {
        //Create the frame
        frame = new JFrame("Stock Control");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 300); // Setting up the size of the frame

        //Table columns
        String[] columnNames = {"Code", "Title", "Description", "Unit Price (Pounds)", "Unit Price (Pence)", "Quantity"};

        //Creating table model
        tableModel = new DefaultTableModel(columnNames, 0);

        //Creating table with the model
        table = new JTable(tableModel);

        //Scrolling pane
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        //Create buy and sell buttons
        buyButton = new JButton("Buy");
        sellButton = new JButton("Sell");

        //Create view sales button
        viewSalesButton = new JButton("View Sales");
        viewSalesButton.setPreferredSize(new Dimension(120, 25));

        //Creting Panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);

        //Another panel for Sales button for proper alignment
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Aligns the button to the top-right
        topPanel.add(viewSalesButton);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        //Initializing the message label
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //Adding the message label to the frame
        frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

        //Add the scroll pane and button panel to the frame
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        updateTableModel(); //Only have to Call this right after loading the CSV data

        buttonPanel.setLayout(new BorderLayout()); //Set layout to BorderLayout
        buttonPanel.add(buyButton, BorderLayout.WEST);
        buttonPanel.add(sellButton, BorderLayout.EAST);

        // Initializing the message label again 
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(messageLabel, BorderLayout.CENTER); //Adding the message label to the button panel

//Add button panel to the frame
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH); // Add the entire panel to the SOUTH
//Initializing the low stock alert        
        LowStockAlert lowStockAlert = new LowStockAlert(false);

        loadCSVData("AshersSportsCollective.csv");

        for (ASCStockItem item : stockItems) {
            item.attachLowStockObserver(lowStockAlert);
        }

        // Now it's time to use the stock items in the application
        updateTableModel();

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String String = JOptionPane.showInputDialog(frame, "Please enter quantity to buy:");
                    try {
                        int quantityToBuy = Integer.parseInt(String);
                        if (quantityToBuy > 0) {
                            int currentQuantity = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());
                            int newquantity = currentQuantity + quantityToBuy;
                            tableModel.setValueAt(newquantity, selectedRow, 5);
                            ASCStockItem selectedItem = stockItems.get(selectedRow);
                            selectedItem.addStock(quantityToBuy);
                            updateTableModel();
                            messageLabel.setText("Purchase successful.");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please enter a positive number for quantity.", "Wrong Quantity", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid number for quantity.", "Wrong Input", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an item to buy.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String quantityString = JOptionPane.showInputDialog(frame, "Enter quantity to sell:");
                    try {
                        int quantityToSell = Integer.parseInt(quantityString);
                        if (quantityToSell > 0) {
                            ASCStockItem selectedItem = stockItems.get(selectedRow);
                            if (selectedItem.getQuantityInStock() >= quantityToSell) {
                                selectedItem.sellStock(quantityToSell);
                                updateTableModel();
                                messageLabel.setText("Sale successful.");
                                recordSaleTransaction(selectedItem.getProductCode(), quantityToSell, selectedItem.getUnitPricePounds() + selectedItem.getUnitPricePence() / 100.0);
                            } else {
                                JOptionPane.showMessageDialog(frame, "Not enough stock is available to sell the specified quantity.", "Insufficient Stock", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please enter a positive number for quantity.", "Wrong Quantity", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid number for quantity.", "Wrong Input", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "An error occurred during the sale: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an item to sell.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        viewSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt for admin credentials when "View Sales" button is clicked
                if (showAdminLoginDialog()) {
                    showSalesData();
                } else {
                    JOptionPane.showMessageDialog(frame, "Access denied. Incorrect admin credentials.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveDataToCSVFiles("AshersSportsCollective.csv", "MengdasSportyMart.csv");
            }
        });

    }

    /**
     * Displays a dialog for admin login and returns a boolean indicating the
     * success.
     *
     * @return true if the login is successful, false otherwise.
     */
    private boolean showAdminLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            // This is a simple check; in a real application, you should use a secure method to check credentials
            return "admin".equals(username) && "admin".equals(password); // replace "adminpassword" with the real admin password
        }
        return false;
    }
    private List<ASCStockItem> stockItems = new ArrayList<>();

    /**
     * Loads stock items from the given CSV file path into the table model.
     *
     * @param csvFilePath The path to the CSV file containing stock items data.
     */
    private void loadCSVData(String csvFilePath) {
        //Define the headers
        String[] headers = {"Code", "Title", "Description", "Unit Price(Pounds)", "Unit Price(Pence)", "Quantity"};

        try (Reader read = new FileReader(csvFilePath)) {
            //Let's Configure CSVFormat to parse without a header and set up the headers manually
            CSVFormat fileFormat = CSVFormat.DEFAULT.withHeader(headers).withSkipHeaderRecord(false);

            CSVParser csvParser = new CSVParser(read, fileFormat);

            // Iterate over the records and add them to the stockItems
            for (CSVRecord record : csvParser) {
                // Create an ASCStockItem object from each record
                String code = record.get("Code");
                String title = record.get("Title");
                String description = record.get("Description");
                int unitPricePounds = Integer.parseInt(record.get("Unit Price(Pounds)"));
                int unitPricePence = Integer.parseInt(record.get("Unit Price(Pence)"));
                int quantity = Integer.parseInt(record.get("Quantity"));

                ASCStockItem item = new ASCStockItem(code, title, description, unitPricePounds, unitPricePence, quantity);

                //add them to the stockItems
                stockItems.add(item);

            }
            List<MSMStockItem> items = MSMStockItem.loadStock();
            for (MSMStockItem msmItem : items) {
                //Wrap each MSMStockItem in an adapter
                ASCStockItem adaptedItem = new MSMStockItemAdapter(msmItem);
                //Add the adapted item to the stockItems
                stockItems.add(adaptedItem);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the table model with the current list of stock items.
     */
    private void updateTableModel() {
        tableModel.setRowCount(0); //To clear existing data

        for (ASCStockItem item : stockItems) {
            tableModel.addRow(new Object[]{
                item.getProductCode(),
                item.getTitle(),
                item.getDescription(),
                item.getUnitPricePounds(),
                item.getUnitPricePence(),
                item.getQuantityInStock()
            });
        }
    }

    /**
     * Records a sale transaction to the sales_transactions.csv file.
     *
     * @param productCode The product code of the item sold.
     * @param quantitySold The quantity of the item sold.
     * @param unitPrice The unit price at which the item was sold.
     */
    private void recordSaleTransaction(String productCode, int quantitySold, double unitPrice) {
        String transactionData = String.format("%s, %s, %d, %.2f\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), productCode, quantitySold, unitPrice);

        try (FileWriter filewriter = new FileWriter("sales_transactions.csv", true); BufferedWriter br = new BufferedWriter(filewriter)) {
            br.write(transactionData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current stock items to the given CSV file paths.
     *
     * @param ascCsvFilePath The file path for Asher's Sport Collective stock
     * items.
     * @param msmCsvFilePath The file path for Mengda's Sportymart stock items.
     */
    private void saveDataToCSVFiles(String ascCsvFilePath, String msmCsvFilePath) {
        List<String> ascLines = new ArrayList<>();
        List<String> msmLines = new ArrayList<>();

        for (ASCStockItem item : stockItems) {
            if (item instanceof MSMStockItemAdapter) {
                MSMStockItem msmItem = ((MSMStockItemAdapter) item).getWrappedMSMStockItem();
                // Format the name to be exactly 60 characters long, padded with spaces if necessary
                String name = String.format("%-60s", msmItem.getName()).substring(0, 60);
                // Ensure the description follows immediately after the name
                String description = msmItem.getDescription();

                // Combine name and description into one field for the CSV
                String nameAndDescription = name + description;

                // Convert MSMStockItem details to a CSV line and add to msmLines
                msmLines.add(String.join(",",
                        String.valueOf(msmItem.getDepartmentId()),
                        msmItem.getCode(),
                        nameAndDescription,
                        String.valueOf(msmItem.getUnitPrice()),
                        String.valueOf(msmItem.getQuantityInStock())));
            } else {
                // Convert ASCStockItem details to a CSV line and add to ascLines
                ascLines.add(String.join(",",
                        item.getProductCode(),
                        item.getTitle(),
                        item.getDescription(),
                        String.valueOf(item.getUnitPricePounds()),
                        String.valueOf(item.getUnitPricePence()),
                        String.valueOf(item.getQuantityInStock())));
            }
        }

        try {
            //Save ASCStockItem lines to AshersSportsCollective.csv
            Files.write(Paths.get(ascCsvFilePath), ascLines);
            //Save MSMStockItem lines to MengdasSportyMart.csv
            Files.write(Paths.get(msmCsvFilePath), msmLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a new frame with sales data loaded from the sales_transactions.csv
     * file.
     */
    private void showSalesData() {
        JFrame salesFrame = new JFrame("Sales Transactions");
        salesFrame.setSize(600, 400);

        //Create table model for sales data
        DefaultTableModel salesTableModel = new DefaultTableModel(new String[]{"Date", "Product Code", "Quantity Sold", "Unit Price"}, 0);
        JTable salesTable = new JTable(salesTableModel);

        //Load data from CSV and add to table model
        try (BufferedReader br = new BufferedReader(new FileReader("sales_transactions.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                salesTableModel.addRow(data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(salesTable);
        salesFrame.add(scrollPane);
        salesFrame.setVisible(true);
    }

    /**
     * Shows a login dialog and returns the role of the logged-in user or
     * "invalid" if the login fails.
     *
     * @return The role of the user as a string.
     */
    private String showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if ("admin".equals(username) && "admin".equals(password)) {
                return "manager";
            } else if ("root".equals(username) && "root".equals(password)) {
                return "user";
            }
        }
        return "invalid";
    }

    /**
     * Makes the StockControlGUI frame visible to the user.
     */
    public void showGUI() {

        frame.setVisible(true); //Set up as true to make the frame visible
    }

    /**
     * The main entry point for the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StockControlGUI app = new StockControlGUI();
                app.showGUI(); // The GUI is now shown to any user without logging in.
            }
        });
    }

    /**
     * Sets the visibility of the "View Sales" button.
     *
     * @param visible true to make the button visible, false to hide it.
     */
    public void setViewSalesButtonVisible(boolean visible) {
        viewSalesButton.setVisible(visible);
    }
}
