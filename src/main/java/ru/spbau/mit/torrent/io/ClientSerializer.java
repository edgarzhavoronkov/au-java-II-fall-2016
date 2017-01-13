package ru.spbau.mit.torrent.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.exceptions.SerializationException;
import ru.spbau.mit.torrent.utils.TorrentFile;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Эдгар on 05.12.2016.
 * Class for serializing client's state to json
 */
@Log4j2
public class ClientSerializer {
    /**
     * Loads state from disk
     * @param workingDir working directory with file
     * @return map from file id to client info
     * @throws SerializationException if IO failed
     */
    public static Map<Long, TorrentFile> loadFiles(String workingDir) throws SerializationException {
        log.info("Loading stored files");
        File stored = new File(workingDir, "client_files.json");
        if (!stored.exists()) {
            return new HashMap<>();
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(stored)) {
            Type type = new TypeToken<Map<Long, TorrentFile>>() { }.getType();
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
    public static void saveFiles(String workingDir, Map<Long, TorrentFile> files) throws SerializationException {
        log.info("Saving stored files");
        File stored = new File(workingDir, "client_files.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (PrintWriter writer = new PrintWriter(stored)) {
            String json = gson.toJson(files);
            writer.println(json);
        } catch (FileNotFoundException e) {
            throw new SerializationException(e);
        }
    }
}
