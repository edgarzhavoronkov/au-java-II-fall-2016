package ru.spbau.mit.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.spbau.mit.model.core.VcsCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by edgar on 05.10.16.
 * Small class for serializing {@link VcsCore} to JSON
 */
public class VcsWriter {
    /**
     * reads {@link VcsCore} from JSON
     * @param file {@link File} to read from
     * @throws IOException
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
