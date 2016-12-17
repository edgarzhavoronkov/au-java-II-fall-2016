package ru.spbau.mit.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.spbau.mit.model.Snapshot;

import java.io.*;

/**
 * Created by Эдгар on 04.10.2016.
 * Small class for reading and writing {@link Snapshot} to disk
 */
public class SnapshotSerializer {
    /**
     * reads {@link Snapshot} from file
     * @param file {@link File} to read from(in Json)
     * @return deserialized {@link Snapshot}
     * @throws IOException
     */
    public static Snapshot readSnapshot(File file) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, Snapshot.class);
        }
    }

    /**
     * writes snapshot to a snapshotFile
     * @param snapshot {@link Snapshot} to serialize
     * @param snapshotFile {@link File} to which snapshot is written
     * @throws IOException
     */
    public static void writeSnapshot(Snapshot snapshot, File snapshotFile) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        snapshotFile.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(snapshotFile)) {
            String json = gson.toJson(snapshot);
            writer.println(json);
        }
    }
}
