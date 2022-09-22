package ggc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Comparator;


public class TransactionSale extends Transaction implements Serializable {
    private int _deadline;
    private double _baseValue;
    private double _valueToPay;
    private Map<Batch, Integer> _batchAmount;
    private int _n;

    public TransactionSale(int id, int date, Partner partner, Product product, int deadline, int amount, int n, Map<String, Product> products) {
        super(id, date, partner, product, amount);
        _deadline = deadline;
        _batchAmount = new HashMap<>();
        product.setBatchesAmount(amount, products, _batchAmount);
        _baseValue = getTransactionValue();
        _valueToPay = _baseValue;
        _paymentDate = -1;
        _n = n;
    }

    public int getDeadline() { return _deadline; }
    public double getBaseValue() { return _baseValue; }
    public double getValueToPay() { return _valueToPay; }
    public int getN() { return _n; }

    public void setPaymentDate(int paymentDate){
        _paymentDate = paymentDate;
    }

    @Override
    public void UpdateValueToPay(int date) {
        _valueToPay = getPartner().calcPriceToPay(date, getBaseValue(), getDeadline(), getN());
    }

    @Override
    public void pay(int date) {
        setPaymentDate(date);
        UpdateValueToPay(date);
        Partner partner = getPartner();

        if (date <= getDeadline()) {
            partner.increasePoints(Math.round(getValueToPay()*10));
            partner.checkPromotion();
        }
        else {
            partner.demote();
        }
    }

    @Override
    public String toString() {
        String str = "VENDA|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + 
        getAmount() + "|" + Math.round(getBaseValue()) + "|" + Math.round(getValueToPay()) + "|" + getDeadline();
        if (getPaymentDate()!=-1) { str += "|" + getPaymentDate(); }
        return str;
    }

    public Map<Batch, Integer> getBatchAmount() { return _batchAmount; }

    @Override
    public double getTransactionValue() {
        double totalPrice = 0;
        double aggregationPrice = 0;

        for (Map.Entry<Batch, Integer> entry : getBatchAmount().entrySet()){
            Batch batch = entry.getKey();
            int quantity = entry.getValue();

            if (batch.getProduct().equals(getProduct())) {
                totalPrice += (batch.getPrice() * quantity);
            }
            else {
                double price = (batch.getPrice() * (1 + getProduct().getAggravation()));
                aggregationPrice += price * getProduct().getIngredientQuantity(batch.getProduct().getId());
                totalPrice += price * quantity;
            }
        }
        if (aggregationPrice != 0 && getProduct().getMaxPrice() < aggregationPrice)
            getProduct().setMaxPrice(aggregationPrice);
        
        return totalPrice;
    }
}
