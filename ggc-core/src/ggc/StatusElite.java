package ggc;

import java.io.IOException;
import java.io.Serializable;

public class StatusElite extends Status implements Serializable {
    public StatusElite(Partner partner) {
        super(partner);
    }

    public void demoted() {
        _partner.decreasePoints(Math.round(_partner.getPoints()*0.75));
        _partner.setStatus(new StatusSelection(_partner));
    }

    @Override
    public String toString() {
        return "ELITE";
    }

    @Override
    public double calcPriceToPay(int date, double basePrice, int deadline, int n) {
        int lowerLimit = deadline - n;
        int upperLimit = deadline + n;

        if (date <= lowerLimit) { return basePrice - basePrice*0.10; }
        else if (date <= deadline) { return basePrice - basePrice*0.10; }
        else if (date <= upperLimit) { return basePrice - basePrice*0.10; }
        else { return basePrice; }
    }

    @Override
    public void checkPromotion() {}
}
