package de.dbconsult.interceptor.exactheight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class FileWriter {

    File outFile;
    BufferedWriter bufferedWriter;

    public FileWriter(String path, String newPostfix) {
        String outFilename = path.substring(0,path.lastIndexOf(".")) + newPostfix + ".nc";
        outFile = new File(outFilename);
        System.out.println(outFilename);
        try {
            outFile.createNewFile();
            bufferedWriter = new BufferedWriter(new java.io.FileWriter(outFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeLine(String line) {
        try {
            bufferedWriter.write(line+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
