package de.dbconsult.interceptor;

import de.dbconsult.interceptor.gpio.GPIOControllerImpl;
import de.dbconsult.interceptor.workflow.GUILogAndPass;
import de.dbconsult.interceptor.workflow.LogAndPassWorkflow;
import de.dbconsult.interceptor.workflow.MonitorSpindleSpeed;
import javafx.application.Application;

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

    private WorkflowRepository() throws Exception {
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
                Class<?> clazz = Class.forName(name);
                if(name.contains("GUI")) {
                    new Thread() {
                        @Override
                        public void run() {
//                            javafx.application.Application.launch((Class<? extends Application>) clazz);
                        }
                    }.start();
                    GUILogAndPass guiLogAndPass = (GUILogAndPass) clazz.newInstance();
                    workflows.add(guiLogAndPass);
                } else if(name.contains("MonitorSpindleSpeed")) {
                    MonitorSpindleSpeed spindleSpeed = (MonitorSpindleSpeed) clazz.newInstance();
                    spindleSpeed.customInit(new GPIOControllerImpl());
                    workflows.add(spindleSpeed);
                } else {
                    workflows.add((Workflow) clazz.newInstance());
                }
            }
        }


    }

    public static WorkflowRepository getInstance() {
        if (instance==null) {
            try {
                instance = new WorkflowRepository();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public ArrayList<Workflow> getConfiguredWorkflows() {
        return workflows;
    }
}
