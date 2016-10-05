package ru.spbau.mit.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.spbau.mit.model.Snapshot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Эдгар on 04.10.2016.
 */
public class SnapshotWriter {
    public static void writeSnapshot(Snapshot snapshot, File snapshotFile) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        snapshotFile.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(snapshotFile)) {
            String json = gson.toJson(snapshot);
            writer.println(json);
        }
    }
}
