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
        MyDatabase db = new MyDatabase(context);
        db.removeAll();
        for (String line : data)
            db.insert(line);
    }

    public List<String> load() {
        return new MyDatabase(context).getAll();
    }
}
