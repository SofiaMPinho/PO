package ggc;

import java.io.IOException;
import java.io.Serializable;

public class NotificationX implements Serializable {
    private Product _product;
    private double _price;
    /** partner can, at any moment, activate or desactive notifications
     * of any type of product
     */

     /** events to consider:
      * -> when the product goes from stock 0 to any other positive number
      * -> when a cheaper batch
      */

    /** NOTA: NOTIFICAÇÕES SÃO REGISTADAS NOS PARCEIROS QUE AS RECEBEM */
    public NotificationX(Batch b){
        _product = b.getProduct();
        _price = b.getPrice();
    }

    @Override
    public String toString(){
        return _product.getId() + "|" + _price;
    }

    public double getPrice(){ return _price; }
    public Product getProduct(){ return _product; }
}