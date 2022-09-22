package ggc;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Comparator;

import ggc.exceptions.OutOfStockProductException;

public class ProductDerivate extends Product implements Serializable{
    
    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Recipe needed to create product */
    private Recipe _recipe;

    /** Price aggravation */
    private double _aggravation;

    public ProductDerivate(String id, double aggravation, Recipe recipe) {
        super(id);
        _n = 3;
        _recipe = recipe;
        _aggravation = aggravation;
    }

    public Recipe getRecipe() {
        return _recipe;
    }

    @Override
    public int getIngredientQuantity(String id) {
        ArrayList<Ingredient> ingredients = getRecipe().getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient.getProductId().equals(id))
                return ingredient.getQuantity();
        }
        return 0;
    }

    @Override
    public double getAggravation() {
        return _aggravation;
    }

    @Override
    public String toString() {
        int maxPrice = (int) Math.round(getMaxPrice());
        double aggravation = getAggravation();

        if ((aggravation - Math.round(aggravation)) == 0)
            aggravation = Math.round(aggravation);
        
        return getId() + "|" + maxPrice + "|" + getCurrentStock() + 
        "|" + aggravation + "|" + getRecipe().toString();
    }

    @Override
    public boolean checkStock(Warehouse warehouse, int requested) throws OutOfStockProductException {
        int stock = warehouse.getTotalProductStock(this);

        if (requested <= stock)
            return true;

        Recipe recipe = getRecipe();
        ArrayList<Ingredient> ingredients = recipe.getIngredients();
        int simpleNeeded = requested - stock;

        for (int i = 0; i < ingredients.size(); i++) {
            int quantity = ingredients.get(i).getQuantity();
            String productSimpleId = ingredients.get(i).getProductId();
            Product productSimple = warehouse.getProductsMapStruct().get(productSimpleId);

            if (!productSimple.checkStock(warehouse, quantity*simpleNeeded)) {
                throw new OutOfStockProductException(productSimpleId, quantity*simpleNeeded, warehouse.getTotalProductStock(productSimple));
            }
        }
        return true;
    }

    @Override
    public void register(Warehouse warehouse, String idPar, double price, int amount) {
        warehouse.registerBatchDerivate(getId(), idPar, price, amount, getAggravation(), getRecipe());
    }

    @Override
    public void setBatchesAmount(int amount, Map<String, Product> products, Map<Batch, Integer> batchAmount) {
        int sold = 0;
        
        TreeSet<Batch> batchesByPrice = new TreeSet<>(BATCH_PRICE_COMPARATOR);
        for (Batch b : getBatches()) {
            batchesByPrice.add(b);
        }

        int amountDerivate = Math.min(getCurrentStock(), amount); 

        decreaseProductStock(amountDerivate);

        for (Batch b : batchesByPrice) {
            if (!b.isStockEmpty()) {
                int min = Math.min(b.getStock(), amountDerivate - sold);
                batchAmount.put(b, min);
                b.decreaseStock(min);
                sold += min;
                if (sold == amount) { return; }
                if (sold == amountDerivate) { break; }
            }
        }

        Recipe recipe = getRecipe();
        ArrayList<Ingredient> ingredients = recipe.getIngredients();
        int simpleNeeded = amount - amountDerivate;

        for (int i = 0; i < ingredients.size(); i++) {
            TreeSet<Batch> batchesSimpleByPrice = new TreeSet<>(BATCH_PRICE_COMPARATOR);
            int quantity = ingredients.get(i).getQuantity()*simpleNeeded;
            String productSimpleId = ingredients.get(i).getProductId();
            Product productSimple = products.get(productSimpleId);
            int simpleSold = 0;

            for (Batch b : productSimple.getBatches())
                batchesSimpleByPrice.add(b);
            
            productSimple.decreaseProductStock(quantity);

            for (Batch b : batchesSimpleByPrice) {
                if (!b.isStockEmpty()) {
                    int min = Math.min(b.getStock(), quantity - simpleSold);
                    batchAmount.put(b, min);
                    b.decreaseStock(min);
                    simpleSold += min;
                    if (simpleSold == quantity) { break; }
                }
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
}
