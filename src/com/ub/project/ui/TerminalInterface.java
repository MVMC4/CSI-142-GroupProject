package com.ub.project.ui;

import com.ub.project.disasters.Disaster;
import com.ub.project.disasters.types.Flood;
import com.ub.project.disasters.types.MedicalIncident;
import com.ub.project.disasters.types.WildFire;
import com.ub.project.dispatch.Dispatch;
import com.ub.project.responders.Responder;
import com.ub.project.responders.units.Ambulance; 
import com.ub.project.responders.units.FireTruck;
import com.ub.project.responders.units.Helicopter;
import com.ub.project.store.DataStore;
import com.ub.project.util.UniqueId;

public class TerminalInterface {
    private InputReader reader = new InputReader();
    private DataStore store;
    private Dispatch dispatch; 
    private UniqueId UID = new UniqueId(); 

    public TerminalInterface(DataStore store) {
        this.store = store;
       // this.dispatch = new Dispatch(store); // Initialize dispatcher with the store
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- DISASTER RESPONSE CONSOLE ---");
            System.out.println("1. Report Disaster");
            System.out.println("2. Add Responder (Unit)");
            System.out.println("3. View Situation Report");
            System.out.println("4. View Responder Fleet"); 
            System.out.println("5. Run Dispatcher");
            System.out.println("6. Exit");

            int choice = reader.getIntInput("Select an option", 1, 6);

            switch (choice) {
                case 1 -> reportDisasterMenu();
                case 2 -> addResponderMenu();
                case 3 -> showSituationReport();
                case 4 -> viewResponderFleet(); 
                case 5 -> runDispatcher();
                case 6 -> running = false;
            }
        }
    }

    private void reportDisasterMenu() {
        System.out.println("\n--- REPORT NEW INCIDENT ---");
        System.out.println("Type: 1. Flood | 2. Fire | 3. Medical");
        int type = reader.getIntInput("Disaster Type", 1, 3);
        String loc = reader.getStringInput("Enter Location");

        if (type == 1) store.addDisaster(new Flood(UID.genDisasterId("Flood"), loc));
        else if (type == 2) store.addDisaster(new WildFire(UID.genDisasterId("Fire"), loc));
        else if (type == 3) store.addDisaster(new MedicalIncident(UID.genDisasterId("Med"), loc));
        
        
        System.out.println("Incident Logged Successfully.");
    }

    private void addResponderMenu() {
        System.out.println("\n--- ADD NEW UNIT TO FLEET ---");
        System.out.println("Type: 1. Helicopter | 2. FireTruck | 3. Ambulance");
        int type = reader.getIntInput("Unit Type", 1, 3);
        
        // Using UID for names too to keep it unique
        if (type == 1) store.addResponder(new Helicopter(UID.genResponderId("Heli")));
        else if (type == 2) store.addResponder(new FireTruck(UID.genResponderId("Fire")));
        else if (type == 3) store.addResponder(new Ambulance(UID.genResponderId("Ambu")));

        System.out.println("Unit added to fleet and is ready for dispatch.");
    }

    private void runDispatcher() {
        System.out.println("\n--- COMMENCING DISPATCH OPERATIONS ---");
        
        if (store.getDisasters().isEmpty()) {
            System.out.println("No incidents to handle.");
        } else {
            for (Disaster disaster : store.getDisasters()) {
                //dispatch.handleDisaster((AbstractDisaster) disaster);
            }
        }

        reader.waitForEnter();
    }
    
    private void showSituationReport() {
        System.out.println("\n=== SITUATION REPORT ===");
        if (store.getDisasters().isEmpty()) {
            System.out.println("No active incidents.");
        } else {
            for (Disaster disaster : store.getDisasters()) {
                System.out.println(disaster);
            }
        }
        reader.waitForEnter();
    }

    private void viewResponderFleet() {
        System.out.println("\n=== CURRENT RESPONDER FLEET ===");
        
        if (store.getResponders().isEmpty()) {
            System.out.println("No units currently in the fleet.");
        } else {
            
            for (Responder responder : store.getResponders()) {
                System.out.println(responder);
            }
        }
        
        reader.waitForEnter();
    }
}