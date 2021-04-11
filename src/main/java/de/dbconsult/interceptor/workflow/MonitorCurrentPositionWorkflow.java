package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

public class MonitorCurrentPositionWorkflow extends AbstractWorkflow {


        private WorkflowDataStore workflowDataStore = null;

        @Override
        public void initialize(WorkflowDataStore workflowDataStore) {
            this.workflowDataStore = workflowDataStore;
            workflowDataStore.update("ProbeInProgress", false);
            workflowDataStore.update("probeAnswerCount", 0);

        }

        @Override
        public synchronized WorkflowResult process(WorkflowResult data) {
            if (isMill(data)) {
                if (getMessage(data).contains("WCO:")) {
                    String msg = getMessage(data);
                    String[] wcoCoords = msg.substring(msg.indexOf("WCO:")+4).split("[,>]");
                    try {
                        workflowDataStore.update("WCO_X", Double.parseDouble(wcoCoords[0]));
                        workflowDataStore.update("WCO_Y", Double.parseDouble(wcoCoords[1]));
                        workflowDataStore.update("WCO_Z", Double.parseDouble(wcoCoords[2]));
                    } catch (NumberFormatException nfe ) {
                        nfe.printStackTrace();
                        System.err.println(msg);
                    }
                }
                if (getMessage(data).contains("MPos:")) {
                    String msg = getMessage(data);
                    String[] mposCoords = msg.substring(msg.indexOf("MPos:")+5).split("[,|]");
                    try {
                        workflowDataStore.update("MPOS_X", Double.parseDouble(mposCoords[0]));
                        workflowDataStore.update("MPOS_Y", Double.parseDouble(mposCoords[1]));
                        workflowDataStore.update("MPOS_Z", Double.parseDouble(mposCoords[2]));
                        workflowDataStore.update("WPOS_X", Double.parseDouble(mposCoords[0]) - (Double) workflowDataStore.read("WCO_X"));
                        workflowDataStore.update("WPOS_Y", Double.parseDouble(mposCoords[1]) - (Double) workflowDataStore.read("WCO_Y"));
                        workflowDataStore.update("WPOS_Z", Double.parseDouble(mposCoords[2]) - (Double) workflowDataStore.read("WCO_Z"));
                    } catch (Exception nfe) {
                        nfe.printStackTrace();
                        System.err.println(msg);
                    }
//                    System.out.println("WPos: " + workflowDataStore.read("WPOS_X") + ", " + workflowDataStore.read("WPOS_Y")+ ", " + workflowDataStore.read("WPOS_Z"));
                }

            }
            return data;
        }

    }
