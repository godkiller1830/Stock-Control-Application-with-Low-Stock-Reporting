/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package existing;

/**
 *
 * @author SATYA NAVEEN
 */


public class MSMStockItemAdapter extends ASCStockItem {

    private final MSMStockItem msmStockItem;

    public MSMStockItemAdapter(MSMStockItem msmStockItem) {
        super(generateProductCode(msmStockItem), 
              msmStockItem.getName(), 
              msmStockItem.getDescription(), 
              msmStockItem.getUnitPrice() / 100, 
              msmStockItem.getUnitPrice() % 100, 
              msmStockItem.getQuantityInStock());
        this.msmStockItem = msmStockItem;
    }

    // Generate a product code that fits the ASCStockItem format
    private static String generateProductCode(MSMStockItem item) {
        // Convert departmentId to department codes, e.g., 1 -> "RUN"
        String departmentCode;
        switch (item.getDepartmentId()) {
            case 1: departmentCode = "RUN"; break;
            case 2: departmentCode = "SWM"; break;
            case 3: departmentCode = "CYC"; break;
            default: departmentCode = "OTHER"; //Unknown
        }
        String paddedCode = String.format("%07d", Integer.parseInt(item.getCode()));
        return departmentCode + paddedCode;
    }

    // Overriding methods which area neccesary
    @Override
    public void sellStock(int quantity) {
        super.sellStock(quantity);
        msmStockItem.setQuanity(msmStockItem.getQuantityInStock() - quantity);
    }

    // Delegate methods
    @Override
    public String getDescription() {
        return msmStockItem.getDescription();
    }
    
    //retrieving the wrapped MSMStockItem instance
    public MSMStockItem getWrappedMSMStockItem() {
        return msmStockItem;
    }
    
    @Override
public void addStock(int quntity) {
    super.addStock(quntity); //Update the adapter's stock
    msmStockItem.setQuanity(msmStockItem.getQuantityInStock() + quntity); //Update the wrapped MSMStockItem's stock
}
}
