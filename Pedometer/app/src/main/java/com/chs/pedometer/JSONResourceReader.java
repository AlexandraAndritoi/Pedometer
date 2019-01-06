package com.chs.pedometer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

public class JSONResourceReader {
    private String jsonString;
    FileInputStream file;

    public JSONResourceReader(String filePath) {
        Writer writer = new StringWriter();
        try {
            file = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jsonString = writer.toString();
    }

    public <T> T constructUsingGson(Class<T> type) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonString, type);
    }
}
