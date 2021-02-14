package de.dbconsult.interceptor.exactheight;

import java.util.ArrayList;

public class HeightsRepository {

    public void storeHeightForLine(long line, double height) {}

    public double getHeightForLine(long line) {
        return 0d;
    }

    public ArrayList<Long> getProbedLineNumbers() {
        return new ArrayList<>();
    }
}
