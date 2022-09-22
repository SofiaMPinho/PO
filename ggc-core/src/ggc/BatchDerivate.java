package ggc;

import java.io.Serializable;

public class BatchDerivate extends Batch implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** Recipe needed to create product */
    private Recipe _recipe;

    /** Batch's aggrevation */
    private double _aggravation;
    
    public BatchDerivate(int stock, double price, Product p, Partner partner, Recipe r, double a) {
        super(stock, price, p, partner);
        _recipe = r;
        _aggravation = a;
    }

    public Recipe getRecipe() {
        return _recipe;
    }

    public double getAggravation() {
        return _aggravation;
    }
}
