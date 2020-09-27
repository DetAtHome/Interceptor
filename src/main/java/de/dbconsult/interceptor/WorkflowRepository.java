package de.dbconsult.interceptor;

import de.dbconsult.interceptor.workflow.LogAndPassWorkflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkflowRepository {

    private ArrayList<Workflow> workflows = new ArrayList<>();
    private static WorkflowRepository instance;

    private WorkflowRepository() {
        List<String> flows = Collections.emptyList();
        try {
            flows = Files.readAllLines(Paths.get("Workflow.txt"));


            if(flows.size()==0) {
                workflows.add(new LogAndPassWorkflow());
            } else {
                for(String name: flows) {
                    if (!name.startsWith("#")) {
                        Class<?> clazz = Class.forName(name);
                        workflows.add((Workflow) clazz.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WorkflowRepository getInstance() {
        if (instance==null) instance = new WorkflowRepository();
        return instance;
    }

    public ArrayList<Workflow> getConfiguredWorkflows() {
        return workflows;
    }
}
