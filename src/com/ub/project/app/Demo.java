package com.ub.project.app;

import java.util.Arrays;
import java.util.ArrayList;

import com.ub.project.disasters.*;
import com.ub.project.disasters.types.*;
import com.ub.project.dispatch.Dispatch;
import com.ub.project.dispatch.Dispatch.DispatchPriority;
import com.ub.project.responders.*;
import com.ub.project.responders.units.*;
import com.ub.project.store.DataStore;
import com.ub.project.util.Logging;

public class Demo {

    public static void main(String[] args) {
  
        Logging logs = new Logging();
        DataStore store = new DataStore();

        System.out.println("--- DISASTER RESPONSE CONSOLE ONLINE ---");

        // 1. Setting the Responders
        // We assign them to variables so we can grab their IDs for the Dispatch records later
        Helicopter rescueAlpha = new Helicopter("Rescue-Alpha");
        Helicopter rescueBravo = new Helicopter("Rescue-Bravo");
        FireTruck engine42 = new FireTruck("Engine-42");
        Ambulance medic1 = new Ambulance("Medic-1");

        store.addResponder(rescueAlpha);
        store.addResponder(rescueBravo);
        store.addResponder(engine42); 
        store.addResponder(medic1);

        logs.postUpdate("Available Units: " + store.getResponders().size());

        System.out.println("\n--- ADDING DISASTERS ---");

        // 2. Setting the Disasters
        Flood brooklynFlood = new Flood("F-101", "Brooklyn");
        store.addDisaster(brooklynFlood);

        WildFire forestFire = new WildFire("WF-202", "Pine Ridge");
        store.addDisaster(forestFire);

        MedicalIncident heartAttack = new MedicalIncident("MI-303", "5th Avenue");
        store.addDisaster(heartAttack);

        // 3. Check Disasters before dispatch
        System.out.println("\n--- CHECKING DISASTERS (PRE-DISPATCH) ---");
        for (Disaster disaster : store.getDisasters()) {
            System.out.println(disaster);
        }

        // 4. Running Dispatches
        System.out.println("\n--- RUNNING DISPATCHES ---");
        
        // Dispatch 1: Flood
        Dispatch d1 = new Dispatch(store, "DSP-001", brooklynFlood.getId(), 
                Arrays.asList(rescueAlpha.getId()), 
                new ArrayList<>(), // No specific resources
                DispatchPriority.HIGH);
        
        // Dispatch 2: Wildfire
        Dispatch d2 = new Dispatch(store, "DSP-002", forestFire.getId(), 
                Arrays.asList(rescueBravo.getId(), engine42.getId()), 
                new ArrayList<>(), 
                DispatchPriority.CRITICAL);
        
        // Dispatch 3: Medical Incident
        Dispatch d3 = new Dispatch(store, "DSP-003", heartAttack.getId(), 
                Arrays.asList(medic1.getId()), 
                new ArrayList<>(), 
                DispatchPriority.MODERATE);

        // Execute the dispatches
        d1.dispatchTeamsAndResources();
        d2.dispatchTeamsAndResources();
        d3.dispatchTeamsAndResources();

        // Print the nice summaries built into the new Dispatch class
        System.out.println(d1.generateDispatchSummary());
        System.out.println(d2.generateDispatchSummary());
        System.out.println(d3.generateDispatchSummary());

        // 5. Check Disasters after dispatch to see status changes (e.g. PARTIAL_RESPONSE / RESPONDING)
        System.out.println("\n--- CHECKING DISASTERS (POST-DISPATCH) ---");
        for (Disaster disaster : store.getDisasters()) {
            System.out.println(disaster);
        }
    }
}