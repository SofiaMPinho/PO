package ggc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Comparator;


public class TransactionBreakdown extends TransactionSale implements Serializable {
    private double _productDerivateVal;
    private double _ingredientsVal;
    ArrayList<Batch> _ingredientBatches;
    public TransactionBreakdown(int id, int date, Partner partner, Product product, int amount, int n, Map<String, Product> products, ArrayList<Batch> ingredientBatches) {
        super(id, date, partner, product, date, amount, n, products);
        getBatchAmount().clear();
        product.setBatchesAmount(amount, products, getBatchAmount());
        _ingredientBatches = ingredientBatches;
        _productDerivateVal = calcBatchesAmountPrice();
        _ingredientsVal = calcIngredientBatchesPrice();
    }

    public ArrayList<Batch> getIngredientBatches(){ return _ingredientBatches; }
    @Override
    public String toString() {
        return "DESAGREGAÇÃO|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + 
        getAmount() + "|" + Math.round(getBaseValue()) + "|" + Math.round(getValueToPay()) + "|" + 
        getTransactionDate() + "|" + getIdQuantityValueStr();
    }

    public String getIdQuantityValueStr(){
        String s = "";
        Batch b;
        int i;
        for (i = 0; i < getIngredientBatches().size()-1; i++){
            b = getIngredientBatches().get(i);
            s += (b.getProduct().getId() + ":" + b.getStock() + ":" + b.getStock()*Math.round(b.getPrice()));
            s += "#";
        }
        b = getIngredientBatches().get(i);
        s += (b.getProduct().getId() + ":" + b.getStock() + ":" + b.getStock()*Math.round(b.getPrice()));
        return s;
    }

    public double getTransactionValue() {
        return _productDerivateVal - _ingredientsVal;
    }

    public double getBaseValue(){
        return _productDerivateVal - _ingredientsVal;
    }

    public double getValueToPay(){
        return _productDerivateVal - _ingredientsVal;
    }

    public double calcBatchesAmountPrice(){
        double totalPrice = 0;
        for (Map.Entry<Batch, Integer> entry : getBatchAmount().entrySet()){
            totalPrice += entry.getValue() * entry.getKey().getPrice();
        }
        return totalPrice;
    }

    public double calcIngredientBatchesPrice(){
        double price = 0;
        for (Batch b : getIngredientBatches()){
            price += (b.getStock()*b.getPrice());
        }
        return price;
    }

    public void setIngredientBatches(ArrayList<Batch> ingredientBatches){
        _ingredientBatches = ingredientBatches;
    }
    
}


