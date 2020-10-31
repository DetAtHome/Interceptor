package de.dbconsult.interceptor;

import java.util.HashMap;

public class WorkflowDataStore {

    private HashMap<String, Object> dataStore = new HashMap<String, Object>();
    private static WorkflowDataStore instance;

    private WorkflowDataStore() {
    }

    public static WorkflowDataStore getInstance() {
        if (instance==null) {
            instance = new WorkflowDataStore();
        }
        return instance;
    }

    public void reset() {
        instance=null;
    }

    public boolean update(String idx, Object data) {
        if (dataStore.containsKey(idx)) dataStore.remove(idx);
        if(data==null) {
            return true;
        }
        dataStore.put(idx, data);
        return true;
    }

    public Object read(String key) {
        if (!dataStore.containsKey(key)) return null;
        return dataStore.get(key);
    }
}
