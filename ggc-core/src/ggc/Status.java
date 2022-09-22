package ggc;

import java.io.IOException;
import java.io.Serializable;

public abstract class Status implements Serializable {
    protected Partner _partner;

    public Status(Partner partner) {
        _partner = partner;
    }
    
    public void promoted() {};
    public void demoted() {};

    @Override
    public abstract String toString();

    public abstract double calcPriceToPay(int date, double basePrice, int deadline, int n);
    public abstract void checkPromotion();
}