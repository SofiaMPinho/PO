package ggc;

import java.io.IOException;
import java.io.Serializable;

public class StatusNormal extends Status implements Serializable {
    public StatusNormal(Partner partner) {
        super(partner);
    }

    public void promoted() {
        _partner.setStatus(new StatusSelection(_partner));
    }

    @Override
    public String toString() {
        return "NORMAL";
    }

    @Override
    public double calcPriceToPay(int date, double basePrice, int deadline, int n) {
        int lowerLimit = deadline - n;
        int upperLimit = deadline + n;

        if (date <= lowerLimit) { return basePrice - basePrice*0.10; }
        else if (date <= deadline) { return basePrice; }
        else if (date <= upperLimit) { return basePrice + basePrice*(0.05*(date-deadline)); }
        else { return basePrice + basePrice*(0.10*(date-upperLimit) + 0.05*n); }
    }

    @Override
    public void checkPromotion() {
        if (_partner.getPoints() > 2000)
            promoted();
        if (_partner.getPoints() > 25000) {
            promoted();
            _partner.getStatus().checkPromotion();
        }
    }
}
