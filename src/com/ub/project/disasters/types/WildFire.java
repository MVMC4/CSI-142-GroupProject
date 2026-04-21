package com.ub.project.disasters.types;

import java.util.List;
import com.ub.project.disasters.AbstractDisaster;

public class WildFire extends AbstractDisaster {

    public WildFire(String id, String location) {
        super(id, location);
    }

    @Override
    public String getType() {
        return "WildFire";
    }

    @Override
    public int getSeverityLevel() {
        return 5; // most severe — requires all responder types
    }

    @Override
    public String getDescription() {
        return "Rapidly spreading fire threatening lives and structures, full emergency response needed";
    }

    @Override
    public List<String> getRequiredResponderTypes() {
        return List.of("FireTruck", "Helicopter", "Ambulance");
    }
}