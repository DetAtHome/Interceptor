package de.dbconsult.interceptor;

import de.dbconsult.interceptor.workflow.GUILogAndPassWorkflow;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WorkflowRepository {

    private ArrayList<Workflow> workflows = new ArrayList<>();
    private WorkflowDataStore workflowDataStore;
    public WorkflowRepository(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        ArrayList<String> flows = new ArrayList(); //Collections.emptyList();
        try {
            flows = (ArrayList<String>) Files.readAllLines(Paths.get("Workflow.txt"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flows.size()==0) {
            // flows.add("de.dbconsult.interceptor.workflow.LogAndPassWorkflow");
            flows.add("de.dbconsult.interceptor.workflow.GUILogAndPass");
        //    workflows.add(new LogAndPassWorkflow());
        }
        for(String name: flows) {
            if (!name.startsWith("#")) {
                Class<Workflow> clazz = null;
                try {
                    clazz = (Class<Workflow>) Class.forName(name);
                    Workflow workflow =  clazz.newInstance();
                    workflow.initialize(workflowDataStore);
                    workflows.add(workflow);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public ArrayList<Workflow> getConfiguredWorkflows() {
        return workflows;
    }
}
