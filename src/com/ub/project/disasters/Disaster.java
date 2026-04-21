package com.ub.project.disasters;

import java.util.List;

public interface Disaster {
    String getType();
    List<String> getRequiredResponderTypes();
    int getSeverityLevel();      // 1 (low) to 5 (critical)
    String getDescription();     // brief summary of the disaster
}