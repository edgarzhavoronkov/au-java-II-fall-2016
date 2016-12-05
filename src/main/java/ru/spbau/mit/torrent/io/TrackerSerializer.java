package ru.spbau.mit.torrent.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.exceptions.SerializationException;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 28.11.2016.
 */
@Log4j2
public class TrackerSerializer {
    public static List<FileInfo> loadFiles(String workingDir) throws SerializationException {
        log.info("Loading stored files");
        File stored = new File(workingDir, "tracker_files.json");
        if (!stored.exists()) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(stored)) {
            Type type = new TypeToken<List<FileInfo>>() { }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static void saveFiles(String workingDir, List<FileInfo> files) throws SerializationException {
        log.info("Saving stored files");
        File stored = new File(workingDir, "tracker_files.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (PrintWriter writer = new PrintWriter(stored)) {
            String json = gson.toJson(files);
            writer.println(json);
        } catch (FileNotFoundException e) {
            throw new SerializationException(e);
        }
    }
}
