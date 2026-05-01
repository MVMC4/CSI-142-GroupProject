package com.ub.project.app;

import com.ub.project.disasters.AbstractDisaster;
import com.ub.project.disasters.Disaster;
import com.ub.project.disasters.DisasterStatus;
import com.ub.project.disasters.types.Flood;
import com.ub.project.disasters.types.MedicalIncident;
import com.ub.project.disasters.types.WildFire;
import com.ub.project.responders.Responder;
import com.ub.project.responders.units.Ambulance;
import com.ub.project.responders.units.FireTruck;
import com.ub.project.responders.units.Helicopter;
import com.ub.project.store.DataStore;

public class DemoApp {

    public static void main(String[] args) {

        DataStore store = new DataStore();

        // 🔹 Add responders
        store.addResponder(new FireTruck("FT1"));
        store.addResponder(new FireTruck("FT2"));
        store.addResponder(new Helicopter("H1"));
        store.addResponder(new Ambulance("A1"));

        // 🔹 Add disasters
        Disaster wildfire = new WildFire("WF1", "Delta Zone");
        Disaster flood = new Flood("F1", "River Bank");
        Disaster medical = new MedicalIncident("M1", "Highway");

        store.addDisaster(wildfire);
        store.addDisaster(flood);
        store.addDisaster(medical);

        // 🔹 Initial state
        System.out.println("\n=== INITIAL STATE ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);

        // 🔹 Dispatch all disasters
        //for (Disaster d : store.getDisasters()) {
            //Dispatch.assignResponders(store, d);
        //}

        // 🔹 After dispatch
        System.out.println("\n=== AFTER DISPATCH ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);

        // 🔹 Simulate completion
        System.out.println("\n=== COMPLETING TASKS ===");

        for (Responder r : store.getResponders()) {
            if (!r.isAvailable()) {
                r.completeTask();
            }
        }

        for (Disaster d : store.getDisasters()) {
            ((AbstractDisaster) d).setStatus(DisasterStatus.RESOLVED);
        }

        // 🔹 Final state
        System.out.println("\n=== FINAL STATE ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);
    }
}