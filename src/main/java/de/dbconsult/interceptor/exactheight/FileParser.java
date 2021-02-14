package de.dbconsult.interceptor.exactheight;

import de.dbconsult.interceptor.WorkflowDataStore;

import java.util.HashMap;
import java.util.Vector;

public class FileParser {

    private WorkflowDataStore workflowDataStore;
    private String downMarker;

    public FileParser(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        downMarker = (String)workflowDataStore.read("MaskDownMarker");
    }
    public void trackXYCoords(String line) {
        if (isMarkerLine(line)) return;
        if(line.startsWith("G0") ||
                line.startsWith("G1") ||
                line.startsWith("G2") ||
                line.startsWith("G3")) {
            Vector<Double> coords = parseLine(line);
            if (coords==null) return;
            workflowDataStore.update("MaskCurrentX", coords.elementAt(0));
            workflowDataStore.update("MaskCurrentY", coords.elementAt(1));
            workflowDataStore.update("MaskCurrentVector", coords);        }

    }

    public boolean isMarkerLine(String line) {
        return line.startsWith(downMarker);
    }

    private Vector<Double> parseLine(String line) {
        if((line.indexOf("X")<0) || (line.indexOf("Y")<0)) return null;
        String[] lines = line.toLowerCase().split(" ");

        Vector<Double> returnVal = new Vector();
        returnVal.add(0,Double.parseDouble(lines[1].substring(1)));
        returnVal.add(1,Double.parseDouble(lines[2].substring(1)));
        return returnVal;
    }
}
