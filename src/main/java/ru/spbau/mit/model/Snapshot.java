package ru.spbau.mit.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Эдгар on 04.10.2016.
 * Abstraction for storing commits
 * We store mapping from filename into hashes in order to compare them in nice way
 */
public class Snapshot {
    private final Map<String, String> filenameToHash = new HashMap<>();

    /**
     * Adds new file to snapshot
     * @param file filename
     * @param hash it's SHA-1 hash
     */
    public void addFile(String file, String hash) {
        filenameToHash.put(file, hash);
    }

    /**
     * Get all the filenames in snapshot
     * @return Set of all filenames in snapshot
     */
    public Set<String> filenameSet() {
        return filenameToHash.keySet();
    }

    /**
     * Gets already computed SHA-1 hash of file
     * @param filename file to get hash of
     * @return String with hash
     */
    public String getFileHash(String filename) {
        return filenameToHash.get(filename);
    }

    /**
     * Checks if given file is present in snapshot
     * @param filename file to check
     * @return True if file is present and False othewise
     */
    public boolean contains(String filename) {
        return filenameToHash.containsKey(filename);
    }
}
