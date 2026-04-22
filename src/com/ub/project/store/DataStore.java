package com.ub.project.store;

import java.util.*;
import java.io.*;

/**
 * DataStore is a central repository for managing application data.
 * It uses the Singleton pattern to ensure only one instance exists.
 */
public class DataStore {
    
    // Static instance for Singleton pattern
    private static DataStore instance;
    
    // In-memory storage using a Map (Key: ID, Value: Object/Data)
    private Map<String, Object> storage;

    // Private constructor prevents instantiation from other classes
    private DataStore() {
        storage = new HashMap<>();
        loadInitialData();
    }

    /**
     * Gets the single instance of DataStore.
     * @return The active DataStore instance.
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Adds or updates an item in the store.
     * @param id Unique identifier for the data.
     * @param data The object to store.
     */
    public void put(String id, Object data) {
        storage.put(id, data);
    }

    /**
     * Retrieves data by ID.
     * @param id The unique identifier.
     * @return The stored object, or null if not found.
     */
    public Object get(String id) {
        return storage.get(id);
    }

    /**
     * Removes an item from the store.
     * @param id The unique identifier to remove.
     */
    public void delete(String id) {
        storage.remove(id);
    }

    /**
     * Returns a list of all stored items.
     */
    public List<Object> getAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Placeholder for loading data from a file or database.
     */
    private void loadInitialData() {
        // Example: put("sys_info", "Project Data Store Active");
        System.out.println("DataStore initialized successfully.");
    }

    /**
     * Clears all data in the store.
     */
    public void clear() {
        storage.clear();
    }
}
