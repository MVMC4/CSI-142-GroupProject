package com.ub.project.store;

import java.util.*;
import java.io.*;

public class DataStore {
    
    private static DataStore instance;
    private Map<String, Object> storage;

    private DataStore() {
        storage = new HashMap<>();
        loadInitialData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void put(String id, Object data) {
        storage.put(id, data);
    }

    public Object get(String id) {
        return storage.get(id);
    }

    public void delete(String id) {
        storage.remove(id);
    }

    public List<Object> getAll() {
        return new ArrayList<>(storage.values());
    }

    private void loadInitialData() {
        System.out.println("DataStore initialized successfully.");
    }

    public void clear() {
        storage.clear();
    }
}
