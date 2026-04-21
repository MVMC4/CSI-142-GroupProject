package com.ub.project.disasters.types;

import java.util.List;
import com.ub.project.disasters.AbstractDisaster;

public class Flood extends AbstractDisaster {

    public Flood(String id, String location) {
        super(id, location);
    }

    @Override
    public String getType() {
        return "Flood";
    }

    @Override
    public int getSeverityLevel() {
        return 3; // moderate — rising water but manageable with aerial support
    }

    @Override
    public String getDescription() {
        return "Rising water levels threatening residential areas, aerial rescue required";
    }

    @Override
    public List<String> getRequiredResponderTypes() {
        return List.of("Helicopter");
    }
}