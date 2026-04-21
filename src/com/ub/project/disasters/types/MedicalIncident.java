package com.ub.project.disasters.types;

import java.util.List;
import com.ub.project.disasters.AbstractDisaster;

public class MedicalIncident extends AbstractDisaster {

    public MedicalIncident(String id, String location) {
        super(id, location);
    }

    @Override
    public String getType() {
        return "MedicalIncident";
    }

    @Override
    public int getSeverityLevel() {
        return 2; // lower severity but time-critical
    }

    @Override
    public String getDescription() {
        return "Time-critical medical emergency requiring immediate paramedic and transport support";
    }

    @Override
    public List<String> getRequiredResponderTypes() {
        return List.of("Ambulance", "Helicopter");
    }
}