package ru.spbau.mit.util;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper around Apache FileUtils
 */
public class FileUtils {
    private static final String VCS_FOLDER_NAME = ".repo";
    private static final String STAGED_FILES_FILENAME = "staged_files";
    private static final String REPO_INFO_FILENAME = "repo_info";

    private final File workingDirectory;

    public FileUtils(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }


    public Path getCurrentDirectory() {
        return null;
    }

    public void mkDir(File currentRepositoryDir) {

    }

    public void clearProject() {

    }
}
