package com.ub.project.responders.units;

import com.ub.project.responders.AbstractResponder;
import com.ub.project.responders.ResponderCategory;
import com.ub.project.responders.ResponderStatus;

public class FireTruck extends AbstractResponder {
    private int waterLevel = 100;

    public FireTruck(String id) {
        super(id, ResponderCategory.FIRE_TRUCK);
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = Math.max(0, Math.min(100, waterLevel));
        if (this.waterLevel == 0) {
            updateStatus(ResponderStatus.RETURNING);
            logs.postUpdate(this, getName() + " needs refill. Status changed to RETURNING.");
        }
    }
}
