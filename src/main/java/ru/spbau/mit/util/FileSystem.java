package ru.spbau.mit.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper around Apache FileSystem
 * so i can easily ask whether repository exists etc.
 */
public class FileSystem {
    public static String getRelativePath(File file, String workingDirectory) {
        return "";
    }

    public static String getNewHash(File file) {
        return "";
    }

    public static Collection<File> listExternalFiles(File file) {
        return Collections.EMPTY_LIST;
    }
}
