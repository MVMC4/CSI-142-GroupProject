package com.ub.project.store;

import java.util.ArrayList;
import java.util.List;

import com.ub.project.disasters.*;
import com.ub.project.responders.Responder;
import com.ub.project.util.Logging;

public class DataStore {

    private List<Disaster> disastersList;
    private List<Responder> respondersList;
    private static final Logging logs = new Logging();

    public DataStore() {
        this.disastersList = new ArrayList<>();
        this.respondersList = new ArrayList<>();
    }

    public void addDisaster(Disaster disaster) {
        disastersList.add(disaster);
        logs.postDebug("Disaster Stored");
    }

    public void addResponder(Responder responder) {
        respondersList.add(responder);
        logs.postDebug("Responder Stored");
    }

    public List<Disaster> getDisasters() {
        return disastersList;
    }

    public List<Responder> getResponders() {
        return respondersList;
    }



    public Disaster findDisasterById(String id) {
        for (Disaster d : disastersList) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    public Responder findResponderByName(String name) {
        for (Responder r : respondersList) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }
}