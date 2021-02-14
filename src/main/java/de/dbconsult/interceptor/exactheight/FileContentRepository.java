package de.dbconsult.interceptor.exactheight;

import java.util.HashMap;

public class FileContentRepository {

    HashMap<Long,String> contents;

    public void setLine(long number, String content) {
        contents.put(number,content);
    }

    public HashMap<Long,String> getContents() {
        return contents;
    }
}
