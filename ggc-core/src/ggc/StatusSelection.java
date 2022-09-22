package ggc;

import java.io.IOException;
import java.io.Serializable;

public class StatusSelection extends Status implements Serializable {
    public StatusSelection(Partner partner) {
        super(partner);
    }

    public void promoted() {
        _partner.setStatus(new StatusElite(_partner));
    }

    public void demoted() {
        _partner.decreasePoints(Math.round(_partner.getPoints()*0.90));
        _partner.setStatus(new StatusNormal(_partner));
    }

    @Override
    public String toString() {
        return "SELECTION";
    }

    @Override
    public double calcPriceToPay(int date, double basePrice, int deadline, int n) {
        int lowerLimit = deadline - n;
        int upperLimit = deadline + n;

        if (date <= lowerLimit) { return basePrice - basePrice*0.10; }
        else if (date <= deadline - 2) { return basePrice - basePrice*0.05; }
        else if (date <= deadline + 1) { return basePrice; }
        else if (date <= upperLimit) { return basePrice + basePrice*(0.02*(date - deadline - 1)); }
        else { return basePrice + basePrice*(0.05*(date - upperLimit) + 0.02*(n-1)); }
    }

    @Override
    public void checkPromotion() {
        if (_partner.getPoints() > 25000)
            promoted();
    }
}
