package com.ub.project.app;

import com.ub.project.store.DataStore;
import com.ub.project.ui.TerminalInterface;

/**
 * Main Entry Point for the Disaster Response System.
 */
public class MainApp {
    public static void main(String[] args) {
        // Initialize the central data store
        DataStore store = new DataStore();
        
        // Initialize the UI with the store
        TerminalInterface ui = new TerminalInterface(store);
        
        // Launch the application loop
        ui.start();
    }
}