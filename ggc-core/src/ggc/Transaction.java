package ggc;

import java.io.IOException;
import java.io.Serializable;

public class Transaction implements Serializable {

    /** The Transaction's id */
    private int _id;

    /** The date when this Transaction ocurred */
    private int _date;

    /** The date when this Transaction was payed for */
    protected int _paymentDate; 

    /** The product that this Transaction refers to */
    private Product _product;

    /** The Transaction's amount of Product */
    private int _amount;

    /** The Transaction's Partner */
    private Partner _partner;

    public Transaction(int id, int date, Partner partner, Product product, int amount){
        _id = id;
        _date = date;
        _partner = partner;
        _product = product;
        _amount = amount;
    }

    public int getId() { return _id; }
    public Product getProduct() { return _product; }
    public Partner getPartner() { return _partner; }
    public int getAmount() { return _amount; }
    public int getPaymentDate() { return _paymentDate; }
    public String getPartnerId() { return getPartner().getId(); }
    public String getProductId() { return getProduct().getId(); }
    public double getTransactionValue() { return 0; }
    public int getTransactionDate(){ return _date; }
    
    public boolean isPayed() { return _paymentDate!=-1; }
    public void pay(int date) {}
    public void UpdateValueToPay(int date) {}
}
