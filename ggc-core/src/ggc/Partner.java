package ggc;

import java.io.Console;
import java.io.IOException;
import java.io.Serializable;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Partner implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** The partner's identifier. */
    private String _id;

    /** The partner's name. */
    private String _name;

    /** The partner's current delivery address */
    private String _address;

    /** The partner's points */
    private long _points;

    /**  The partner's notifications */
    private ArrayList<NotificationX> _notifications;

    /** The partner's transactions */
    private Map<Integer, Transaction> _transactionsPurchase;

    /** The partner's transactions */
    private Map<Integer, Transaction> _transactionsSaleAndDesaggregation;

    /** The partner's status */
    private Status _status = new StatusNormal(this);

    /** The List of Products this Partner wants to get notifications about */
    private Map<String, Product> _notifiedProducts;

    /** Partner's Delivery Method */
    private DeliveryMethod _deliveryMethod;
    
    /** Constructor */
    public Partner(String id, String name, String address, Map<String, Product> notifiedProducts) {
        _id = id;
        _name = name;
        _address = address;
        _notifiedProducts = notifiedProducts;
        _notifications = new ArrayList<>();
        _transactionsPurchase = new TreeMap<>();
        _transactionsSaleAndDesaggregation = new TreeMap<>();
    }

    public void setStatus(Status status) {
        _status = status;
    }

    public String getId() { return _id; }
    public String getName() { return _name; }
    public String getAddress() { return _address; }
    public long getPoints() { return _points; }
    public Status getStatus() { return _status; }
    public ArrayList<NotificationX> getNotifications() { return _notifications; }
    public Map<Integer, Transaction> getTransactionsPurchase() { return _transactionsPurchase; }
    public Map<Integer, Transaction> getTransactionsSaleAndDesaggregation() { return _transactionsSaleAndDesaggregation; }

    public void increasePoints(Long points) {
        _points += points;
    }
    
    public void decreasePoints(long points) {
        _points -= points;
    }

    public void checkPromotion() {
        _status.checkPromotion();
    }

    public void demote() {
        _status.demoted();
    }
    
    public double getPurchasesValue() { 
        double purchaseVal = 0;
        for (Map.Entry<Integer, Transaction> entry : getTransactionsPurchase().entrySet()){
            purchaseVal += entry.getValue().getTransactionValue();
        }
        return purchaseVal; 
    } 

    public double getSalesValue() { 
        double salesVal = 0;
        for (Map.Entry<Integer, Transaction> entry : getTransactionsSaleAndDesaggregation().entrySet()){
            salesVal += entry.getValue().getTransactionValue();
        }
        return salesVal; 
    }
    public int salesComplete() { 
        double salesVal = 0;
        for (Map.Entry<Integer, Transaction> entry : getTransactionsSaleAndDesaggregation().entrySet()){
            if (entry.getValue().isPayed()){
                salesVal += entry.getValue().getTransactionValue();
            }
        }
        return (int) Math.round(salesVal); 
     } //change

    @Override
    public String toString() {
        return _id + "|" + _name + "|" + _address + "|" + _status.toString() + "|" + _points + "|" + Math.round(getPurchasesValue()) +
        "|" + Math.round(getSalesValue()) + "|" + this.salesComplete();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Partner) {
            Partner partner = (Partner) o;
            if (_id.equals(partner.getId()))
                return true;
        }
        return false;
    }

    public void addTransactionPurchase(int id, Transaction t) {
        _transactionsPurchase.put(id, t);
    }

    public void addTransactionSaleAndDesaggregation(int id, Transaction t) {
        _transactionsSaleAndDesaggregation.put(id, t);
    }

    public double calcPriceToPay(int date, double basePrice, int deadline, int n) {
        return _status.calcPriceToPay(date, basePrice, deadline, n);
    }

    public Map<String, Product> getNotifiedProducts() {
        return _notifiedProducts;
    }

    public Boolean isProductNotifiable(Product p) {
        return getNotifiedProducts().containsKey(p.getId());
    }
    public void clearNotifications() {
        _notifications = new ArrayList<>();
    }

    public void addNotifiedProduct(Product p) {
        _notifiedProducts.put(p.getId(), p);
    }

    public void removeNotifiedProduct(Product p) {
        _notifiedProducts.remove(p.getId());
    }

    public void addNotification(NotificationX notif){
        getNotifications().add(notif);
    }
}