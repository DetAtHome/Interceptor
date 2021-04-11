package de.dbconsult.interceptor.internal;

import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.exactheight.FileReader;
import de.dbconsult.interceptor.exactheight.FileWriter;

import java.io.File;
import java.util.HashMap;

public class SettingsRepository {

    public HashMap<String, Double> readOffsets() {
        HashMap<String, Double> offsets = new HashMap<>();
        FileReader reader = new FileReader(new File("Settings.txt"));
        try {
            String line="";
            do {
                line = reader.line();
                if(line==null) break;
                String[] data = line.split("=");
                offsets.put(data[0],Double.parseDouble(data[1]));
            } while (line!=null);

        } catch (NumberFormatException nfe) {
            // save to ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offsets;
    }

    public void writeOffsets(WorkflowDataStore workflowDataStore) {
        try {
            FileWriter writer = new FileWriter("Settings.txt","");
            writer.writeLine("WorkpieceXOffset=" + workflowDataStore.read("WorkpieceXOffset"));
            writer.writeLine("WorkpieceYOffset=" + workflowDataStore.read("WorkpieceYOffset"));
            writer.writeLine("CurrentWorkpieceOffsetX=" + workflowDataStore.read("CurrentWorkpieceOffsetX"));
            writer.writeLine("CurrentWorkpieceOffsetY=" + workflowDataStore.read("CurrentWorkpieceOffsetY"));
            writer.writeLine("FwdButtonOffsetX=" + workflowDataStore.read("FwdButtonOffsetX"));
            writer.writeLine("FwdButtonOffsetY=" + workflowDataStore.read("FwdButtonOffsetY"));
            writer.writeLine("RvsButtonOffsetX=" + workflowDataStore.read("RvsButtonOffsetX"));
            writer.writeLine("RvsButtonOffsetY=" + workflowDataStore.read("RvsButtonOffsetY"));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
