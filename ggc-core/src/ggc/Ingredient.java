package ggc;

import java.io.IOException;
import java.io.Serializable;

public class Ingredient implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Name of the Ingredient */
    private String _name;

    private double _val;
    /** Quantity of the ingredient */
    private int _quantity;

    public Ingredient(String productId, int quantity) {
        _name = productId;
        /** go get product by name/id */
        _quantity = quantity;
    }

    public String getProductId() {
        return _name;
    }

    public void setValue(double val){ _val = val; }
    public double getValue(double val){ return _val; }

    public int getQuantity() {
        return _quantity;
    }

    @Override
    public String toString() {
        return _name + ":" + _quantity;
    }
}
