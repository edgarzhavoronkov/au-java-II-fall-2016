package ru.spbau.mit.torrent.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.exceptions.SerializationException;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эдгар on 28.11.2016.
 * Class for serializing tracker's state to json
 */
@Log4j2
public class TrackerSerializer {
    /**
     * Loads state from disk
     * @param workingDir working directory with file
     * @return map from file id to client info
     * @throws SerializationException if IO failed
     */
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

    /**
     * Saves state to disk
     * @param workingDir working directory with file
     * @param files map from file id to client info
     * @throws SerializationException if IO failed
     */
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
