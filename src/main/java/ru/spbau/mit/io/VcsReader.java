package ru.spbau.mit.io;

import com.google.gson.Gson;
import ru.spbau.mit.model.core.VcsCore;

import java.io.*;

/**
 * Created by edgar on 05.10.16.
 */
public class VcsReader {
    public static VcsCore read(File file) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, VcsCore.class);
        }
    }
}
