package com.ub.project.responders;

public interface Responder {
    String getName();
    boolean isAvailable();
    void dispatchTo(String location);
    ResponderCategory getResponderCategory(); 
    void completeTask();
    void updateStatus(ResponderStatus status);
    ResponderStatus getStatus();
} 
