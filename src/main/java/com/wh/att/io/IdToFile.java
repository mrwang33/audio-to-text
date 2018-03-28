package com.wh.att.io;

import java.io.*;

public class IdToFile {
    public static void saveId(String id) {
        try {
            //Whatever the file path is.
            FileWriter writer=new FileWriter("/upload/taskid/taskId.txt",true);
            writer.write(id+"        ");
            writer.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }

    }

}
