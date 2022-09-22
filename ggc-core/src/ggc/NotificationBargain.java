package ggc;

import java.io.Serializable;

public class NotificationBargain extends NotificationX implements Serializable {
    /** partner can, at any moment, activate or desactive notifications
     * of any type of product
     */

     /** events to consider:
      * -> when the product goes from stock 0 to any other positive number
      * -> when a cheaper batch
      */

    /** NOTA: NOTIFICAÇÕES SÃO REGISTADAS NOS PARCEIROS QUE AS RECEBEM */

    public NotificationBargain(Batch b){
        super(b);
    }

    @Override
    public String toString(){
        return "BARGAIN|" + getProduct().getId() + "|" + Math.round(getPrice());
    }
}