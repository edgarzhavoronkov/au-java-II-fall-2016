package ru.spbau.mit.io;

import com.google.gson.Gson;
import ru.spbau.mit.model.Snapshot;

import java.io.*;

/**
 * Created by Эдгар on 04.10.2016.
 * Small class for reading {@link Snapshot} from disk
 */
public class SnapshotReader {
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
}
