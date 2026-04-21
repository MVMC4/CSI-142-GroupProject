package com.ub.project.responders;

import com.ub.project.util.Logging;

public abstract class AbstractResponder implements Responder {
    private String id;
    private ResponderCategory category;
    private ResponderStatus status = ResponderStatus.IDLE;
    protected static final Logging logs = new Logging();

    protected AbstractResponder(String id, ResponderCategory category) {
        this.id = id;
        this.category = category;
        logs.postUpdate("New Responder: [" + category + "] ID: " + id + " has joined the disaster-response.");
    }

    @Override
    public String getName() {
        return category + "-" + id;
    }

    @Override
    public ResponderCategory getResponderCategory() {
        return category;
    }

    @Override
    public boolean isAvailable() {
        return status == ResponderStatus.IDLE;
    }

    @Override
    public ResponderStatus getStatus() {
        return status;
    }

    @Override
    public void updateStatus(ResponderStatus status) {
        this.status = status;
        logs.postUpdate(this, "Unit " + getName() + " is now " + this.status);
    }

    @Override
    public void dispatchTo(String location) {
        updateStatus(ResponderStatus.DISPATCHED);
        logs.postUpdate(this, getName() + " dispatched to " + location);
    }

    @Override
    public void completeTask() {
        if (this.status == ResponderStatus.ON_SITE || this.status == ResponderStatus.DISPATCHED) {
            updateStatus(ResponderStatus.RETURNING);
            updateStatus(ResponderStatus.IDLE);
        }
    }

    @Override
    public String toString() {
        return String.format("RESPONDER [" + getName() + "] | Status: " + getStatus());
    }
}