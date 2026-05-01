package com.ub.project.responders.units;

import com.ub.project.responders.AbstractResponder;
import com.ub.project.responders.ResponderCategory;
import com.ub.project.responders.ResponderStatus;

public class Ambulance extends AbstractResponder {

    private static final int MAX_KITS = 10;
    private int medicalKits = MAX_KITS;

    public Ambulance(String id) {
        super(id, ResponderCategory.AMBULANCE);
    }

    public int getMedicalKits() { return medicalKits; }

    public void useMedicalKit() {
        if (medicalKits <= 0)
            throw new IllegalStateException(getName() + " has no medical kits remaining.");
        medicalKits--;
        logs.postUpdate(this, getName() + " used 1 kit. Remaining: " + medicalKits);
        if (medicalKits == 0) {
            updateStatus(ResponderStatus.RETURNING);
            logs.postUpdate(this, getName() + " is out of kits — returning to base.");
        }
    }

    public void restock() {
        medicalKits = MAX_KITS;
        logs.postUpdate(this, getName() + " restocked to " + MAX_KITS + " kits.");
    }

    @Override
    public String toString() {
        return super.toString() + " | Kits: " + medicalKits + "/" + MAX_KITS;
    }
}
