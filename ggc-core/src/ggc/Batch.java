package ggc;

import java.io.Serializable;

public class Batch implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Batch's stock */
    private int _stock;

    /** Batch's price per Product */
    private double _unit_price;
    
    /** Batch's Product */
    private Product _product;

    /** Batch's Partner */
    private Partner _partner;

    public Batch(int stock, double price, Product p, Partner partner) {
        _stock = stock;
        _unit_price = price;
        _product = p;
        _partner = partner;
    }

    public int getStock() { return _stock; }
    public double getPrice() { return _unit_price; }
    public Product getProduct() { return _product; }
    public Partner getPartner() { return _partner; }
    
    public void decreaseStock(int sold) {
        _stock -= sold;
    }

    @Override
    public String toString() {
        /** idProduto|idParceiro|preço|stock-actual */
        int price = (int) Math.round(getPrice());
        return getProduct().getId() + "|" + getPartner().getId() + "|" + price + "|" + getStock();
    }
    public NotificationX createNotification(){
        Product product = getProduct();
        NotificationX notif = null;
        /** verify if stock of product is 0 and if so, create notification */
        if (product.isStockEmpty()){
            /** new NEW Notification */
            notif = new NotificationNew(this);
        }
        /** verify if price of this batch is lower than of the other batches */
        else if (getPrice() < product.getLowestPrice()){
            /** new BARGAIN Notification */
            notif = new NotificationBargain(this);
        }
        return notif;
    }
    public boolean isStockEmpty(){
        return getStock() == 0;
    }
}
