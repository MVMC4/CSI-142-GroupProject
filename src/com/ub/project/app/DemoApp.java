package com.ub.project.app;

import com.ub.project.dispatch.Dispatch;
import com.ub.project.disasters.*;
import com.ub.project.disasters.types.*;
import com.ub.project.responders.Responder;
import com.ub.project.responders.units.*;
import com.ub.project.store.DataStore;

import java.util.List;

public class DemoApp {

    public static void main(String[] args) {

        DataStore store = new DataStore();

        // Responders
        store.addResponder(new FireTruck("FT1"));
        store.addResponder(new FireTruck("FT2"));
        store.addResponder(new Helicopter("H1"));
        store.addResponder(new Ambulance("A1"));

        // Disasters
        Disaster wildfire = new WildFire("WF1", "Delta Zone");
        Disaster flood = new Flood("F1", "River Bank");
        Disaster medical = new MedicalIncident("M1", "Highway");

        store.addDisaster(wildfire);
        store.addDisaster(flood);
        store.addDisaster(medical);

        // Initial
        System.out.println("\n=== INITIAL ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);

        // DISPATCH
        Dispatch dispatch = new Dispatch(
                store,
                "D1",
                "WF1",
                List.of("FIRETRUCK-FT1", "HELICOPTER-H1"),
                List.of(),
                Dispatch.DispatchPriority.HIGH
        );

        System.out.println("\n=== DISPATCHING ===");
        dispatch.dispatchTeamsAndResources();

        System.out.println(dispatch.generateDispatchSummary());

        // After dispatch
        System.out.println("\n=== AFTER DISPATCH ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);

        // COMPLETE
        System.out.println("\n=== COMPLETING ===");
        dispatch.updateStatus(Dispatch.DispatchStatus.COMPLETED);

        // Final
        System.out.println("\n=== FINAL ===");
        store.getResponders().forEach(System.out::println);
        store.getDisasters().forEach(System.out::println);
    }
}