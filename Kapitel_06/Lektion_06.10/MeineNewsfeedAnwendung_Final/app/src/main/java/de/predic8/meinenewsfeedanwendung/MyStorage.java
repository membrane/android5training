package de.predic8.meinenewsfeedanwendung;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MyStorage {
    private final Context context;

    public MyStorage(Context context) {
        this.context = context;
    }

    public void store(List<String> data) {
        try {
            FileOutputStream fos = context.openFileOutput("data.txt", Context.MODE_PRIVATE);
            try {
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                for (String line : data)
                    osw.write(line + "\n");
                osw.flush();
            } finally {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    public List<String> load() {
        try {
            FileInputStream fis = context.openFileInput("data.txt");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                ArrayList<String> result = new ArrayList<String>();
                String line;
                while ((line = br.readLine()) != null)
                    result.add(line);
                return result;
            } finally {
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
        return null;
    }
}
