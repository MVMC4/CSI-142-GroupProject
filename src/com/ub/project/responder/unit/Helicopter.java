package com.ub.project.responders.units;

import com.ub.project.responders.AbstractResponder;
import com.ub.project.responders.ResponderCategory;

public class Helicopter extends AbstractResponder {
    private double fuelLevel = 100.0;
    private int maxRange = 500;

    public Helicopter(String id) {
        super(id, ResponderCategory.HELICOPTER);
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }
}