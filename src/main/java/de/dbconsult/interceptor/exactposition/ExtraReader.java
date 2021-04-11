package de.dbconsult.interceptor.exactposition;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.serial.SerialCommunication;

public class ExtraReader {
    WorkflowDataStore workflowDataStore;

    public ExtraReader(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
    }

    public void initializeHeight() {
        SerialCommunication extra = getCommDescription();
        extra.write("h;");
        String answer = "";
        while(!answer.toLowerCase().contains("ok")) {
            WorkflowResult result =extra.readFully();
            answer = new String(result.getOutput(),0,result.getLen());
        }

    }
    public long readHeightFromExtra() {
        SerialCommunication extra = getCommDescription();
        extra.write("h;");
        String answer = "";
        while(! answer.toLowerCase().contains("h")) {
            WorkflowResult result =extra.readFully();
            answer = new String(result.getOutput(),0,result.getLen());
            if (answer.toLowerCase().contains("alarm") || answer.toLowerCase().contains("error"))
                throw new RuntimeException("Unexpected machining error or alarm: " + answer);
        }
        long returnVal = 0l;
        int startIdx = 2;
        try {
            while (answer.substring(startIdx).startsWith("0")) startIdx++;
            String trimmed = answer.substring(startIdx).trim();
            returnVal = Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnVal;
    }

    private SerialCommunication getCommDescription() {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        SerialCommunication toComm = serialsRepository.getExtra().getComm();
        return toComm;
    }
}
