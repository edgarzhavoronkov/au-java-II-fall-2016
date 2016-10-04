package ru.spbau.mit.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Эдгар on 04.10.2016.
 */
public class Snapshot {
    private final Map<String, String> filenameToHash = new HashMap<>();

    public void addFile(String file, String hash) {
        filenameToHash.put(file, hash);
    }

    public Set<String> filenameSet() {
        return filenameToHash.keySet();
    }

    public String getFileHash(String filename) {
        return filenameToHash.get(filename);
    }

    public boolean contains(String filename) {
        return filenameToHash.containsKey(filename);
    }
}
