package ggc;

import java.io.IOException;
import java.io.Serializable;

public class TransactionPurchase extends Transaction implements Serializable {
    private double _price;

    public TransactionPurchase(int id, int date, Partner partner, Product product, double price, int amount) {
        super(id, date, partner, product, amount);
        _paymentDate = date;
        _price = price;
    }

    public double getPrice() { return _price; }
    public double getTransactionValue() { 
        return getAmount()*getPrice(); 
    }

    @Override
    public String toString(){
        return "COMPRA|" + getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + getAmount() + "|" + Math.round(getTransactionValue()) + "|" + getPaymentDate();
    }
    public void pay(int day) {}
}
