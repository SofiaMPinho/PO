package ggc;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Comparator;

import ggc.exceptions.OutOfStockProductException;

public class Product implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Product's ID */
    private String _id;

    /** TreeSet of Batches of this Product with Comparator */
    private TreeSet<Batch> _batches;

    /** Product's max price */
    private double _maxPrice;

    /** Product's current stock */
    private int _currentStock;

    protected int _n;

    public Product(String id) {
        _id = id;
        _maxPrice = 0;
        _currentStock = 0;
        _batches = new TreeSet<>(BATCH_COMPARATOR);
        _n = 5;
    }

    /** Getters and Setters*/
    public String getId() {
        return _id;
    }

    public TreeSet<Batch> getBatches() {
        return _batches;
    }

    public int getCurrentStock() {
        return _currentStock;
    }

    public double getMaxPrice() {
        return _maxPrice;
    }

    public int getN() {
        return _n;
    }

    public double getAggravation() {
        return 0;
    }

    public void setMaxPrice(double p) {
        _maxPrice = p;
    }

    public void addBatches(Batch b) {
        _batches.add(b);
        /** update stock */
        _currentStock = _currentStock + b.getStock();
        /** update maxPrice */
        if (b.getPrice() > _maxPrice){
            setMaxPrice(b.getPrice());
        }
    }

    public void deleteBatch(Batch b){
        getBatches().remove(b);
    }

    @Override
    public String toString() {
        int maxPriceInt = (int) Math.round(_maxPrice);
        return _id + "|" + maxPriceInt + "|" + _currentStock;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product) {
            Product product = (Product) o;
            return product.getId().equals(this.getId());
        }
        return false;
    }

    public static final Comparator<Batch> BATCH_COMPARATOR = (Comparator<Batch>&Serializable) (a, b) -> {
            /** id do produto ->  id do parceiro -> 
             * -> ordem crescente de preço -> ordem crescente de existências */
            /** comparing by product id */
            int cmp0 = a.getProduct().getId().compareTo(b.getProduct().getId());
            if (cmp0 != 0) {
                return cmp0;
            }
            /** comparing by partner id */
            int cmp1 = a.getPartner().getId().compareTo(b.getPartner().getId());
            if (cmp1 != 0) {
                return cmp1;
            }
            /** comparing by ascending order of price */
            int cmp2 = (int) (a.getPrice() - b.getPrice());
            if (cmp2 != 0) {
                return cmp2;
            }
            return a.getStock() - b.getStock();
    };

    public boolean isStockEmpty(){
        for (Batch b : getBatches()){
            if (!b.isStockEmpty()){return false;}
        }
        return true;
    }

    public double getLowestPrice(){
        double lowestPrice = getMaxPrice();
        for (Batch b : getBatches()){
            if (b.getPrice() < lowestPrice && b.getStock()!=0){
                lowestPrice = b.getPrice();
            }
        }
        return lowestPrice;
    }

    public void register(Warehouse warehouse, String idPar, double price, int amount) {
        warehouse.registerBatchSimple(getId(), idPar, price, amount);
    }

    public boolean checkStock(Warehouse warehouse, int requested) throws OutOfStockProductException {
        return requested <= warehouse.getTotalProductStock(this);
    }

    public void decreaseProductStock(int quantity){ _currentStock -= quantity; }

    public void setBatchesAmount(int amount, Map<String, Product> products, Map<Batch, Integer> batchAmount) {
        int sold = 0;
        
        TreeSet<Batch> batchesByPrice = new TreeSet<>(BATCH_PRICE_COMPARATOR);
        for (Batch b : getBatches()) {
            batchesByPrice.add(b);
        }

        decreaseProductStock(amount);

        for (Batch b : batchesByPrice) {
            if (!b.isStockEmpty()) {
                int min = Math.min(b.getStock(), amount - sold);
                batchAmount.put(b, min);
                /** delete batch if stock is zero, else just decrease stock */
                if (b.getStock()==min){ this.deleteBatch(b); }
                else{ b.decreaseStock(min); }
                sold += min;
                if (sold==amount) { return; }
            }
        }
    }

    public static final Comparator<Batch> BATCH_PRICE_COMPARATOR = (Comparator<Batch>&Serializable) (a, b) -> {
        /** comparing by ascending order of price */
        int cmp2 = (int) (a.getPrice() - b.getPrice());
        if (cmp2 != 0) {
            return cmp2;
        }
        return a.getStock() - b.getStock();
    };
    
    public Recipe getRecipe() {
        return null;
    }

    public int getIngredientQuantity(String id) {
        return 0;
    }
}