package de.dbconsult.interceptor.exactheight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class FileReader {

    File readFile;
    BufferedReader bufferedReader;
    FileContentRepository fileContentRepository;
    public FileReader(File file) {
        try {
            readFile = file;
            bufferedReader = new BufferedReader(new java.io.FileReader(readFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String line() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void readToRepo() {
        String line;
        long currentLine = 0;
        try {
           do {
                line = bufferedReader.readLine();
                currentLine++;
                fileContentRepository.setLine(currentLine, line);
            } while (line!=null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
