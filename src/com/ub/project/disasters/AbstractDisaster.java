package com.ub.project.disasters;

import com.ub.project.util.Logging;

public abstract class AbstractDisaster implements Disaster {
    private String id;
    private String location;
    private DisasterStatus status = DisasterStatus.PENDING;

    private static final Logging logs = new Logging();

    protected AbstractDisaster(String id, String location) {
        this.id = id;
        this.location = location;
        logs.postUpdate("New " + getType() + " reported at " + location + 
                        " (ID: " + id + ") | Severity: " + getSeverityLevel() + "/5");
    }

    public void setStatus(DisasterStatus newStatus) {
        logs.postUpdate(this, "Status changing from " + this.status + " to " + newStatus);
        this.status = newStatus;
    }

    public DisasterStatus getStatus() { return status; }
    public String getId() { return id; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return "DISASTER [" + getType() + "]" +
               " | ID: " + getId() +
               " | Loc: " + getLocation() +
               " | Status: " + getStatus() +
               " | Severity: " + getSeverityLevel() + "/5" +
               " | Info: " + getDescription() +
               " | Needs: " + getRequiredResponderTypes();
    }
}