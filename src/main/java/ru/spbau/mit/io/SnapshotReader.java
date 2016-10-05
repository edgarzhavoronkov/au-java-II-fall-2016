package ru.spbau.mit.io;

import com.google.gson.Gson;
import ru.spbau.mit.model.Snapshot;

import java.io.*;

/**
 * Created by Эдгар on 04.10.2016.
 */
public class SnapshotReader {
    public static Snapshot readSnapshot(File file) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, Snapshot.class);
        }
    }
}
