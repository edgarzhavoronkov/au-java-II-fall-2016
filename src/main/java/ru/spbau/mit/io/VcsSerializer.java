package ru.spbau.mit.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.spbau.mit.model.core.VcsCore;

import java.io.*;

/**
 * Created by edgar on 05.10.16.
 * Small class for serializing and deserializing {@link VcsCore} to JSON
 */
public class VcsSerializer {
    /**
     * reads {@link VcsCore} from JSON
     * @param file {@link File} to read from
     * @return {@link VcsCore} instance
     * @throws IOException if gson failed
     */
    public static VcsCore read(File file) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, VcsCore.class);
        }
    }

    /**
     * writes state of {@link VcsCore} into JSON
     * @param file {@link File} to write into
     * @throws IOException if failure happened
     */
    public static void write(VcsCore core, File file) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        file.getParentFile().mkdirs();
        try (PrintWriter writer = new PrintWriter(file)) {
            String json = gson.toJson(core);
            writer.println(json);
        }
    }
}
