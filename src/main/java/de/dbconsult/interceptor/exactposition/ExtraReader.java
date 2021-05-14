package de.dbconsult.interceptor.exactposition;

import de.dbconsult.interceptor.Communication;
import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import static de.dbconsult.interceptor.internal.Reversed.reversed;
import java.util.ArrayList;


public class ExtraReader {

    private boolean readingPaused = false;

    WorkflowDataStore workflowDataStore;
    private ArrayList<String> lastLine = new ArrayList<>();
    Communication extra = null;

    long logLine=0;

    public ExtraReader(WorkflowDataStore workflowDataStore) {

        this.workflowDataStore = workflowDataStore;
        extra = getCommDescription();
    }

    public void initializeHeight() {
        extra.write("h;");
        String answer = "";
        readingPaused = true;
        while(!answer.toLowerCase().contains("ok")) {

            WorkflowResult result =extra.readFully("toextra");
            answer = new String(result.getOutput(),0,result.getLen());
            lastLine.add(answer.trim());
        }
        readingPaused = false;

    }
    public long readHeightFromExtra() {
        extra.write("h;");
        String answer = "";
        readingPaused = true;
        while(! answer.toLowerCase().contains("h")) {
            WorkflowResult result =extra.readFully("toextra");
            answer = new String(result.getOutput(),0,result.getLen());
            lastLine.add(answer.trim());
            if (answer.toLowerCase().contains("alarm") || answer.toLowerCase().contains("error")) {
                readingPaused = false;
                throw new RuntimeException("Unexpected machining error or alarm: " + answer);
            }
        }
        readingPaused = false;
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

    public void send(String toSend) {
        if(null!=toSend) extra.write(toSend);
    }

    public String getLastLog(boolean excludeHeartbeat) {
        String bufferdLines = "";
        if(!readingPaused) {
            WorkflowResult result =extra.readFully("toextra");
            String answer = new String(result.getOutput(),0,result.getLen());
            if (!answer.contains("undefined")) {
                if (answer.trim().length() > 0)
                    lastLine.add(answer.trim());
            }
        }
        for (String line: reversed(lastLine)) {
            logLine++;
            if(!(excludeHeartbeat && line.contains("Heartbeat")))

                bufferdLines = bufferdLines + "\n" + line;
        }

        lastLine.clear();

        return bufferdLines;
    }
    private Communication getCommDescription() {
        SerialsRepository serialsRepository = (SerialsRepository) workflowDataStore.read("SerialsRepository");
        Communication toComm = serialsRepository.getExtra().getComm();
        return toComm;
    }
}
