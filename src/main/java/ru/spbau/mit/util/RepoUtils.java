package ru.spbau.mit.util;

import ru.spbau.mit.model.FileInfo;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 01.10.2016.
 * Wrapper around Repository for serializing and deserializing repository
 */
public class RepoUtils {
    private final FileUtils fileUtils;

    public RepoUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public Repository loadRepositoryFromDisk() {
        return null;
    }

    public boolean isInit() {
        return false;
    }

    public void addToStagedFiles(List<String> addedFileNames) {

    }

    public boolean haveUncommittedChanges() {
        return false;
    }

    public Map<FileInfo, Commit> collectChanges(Repository repository) {
        return null;
    }

    public Map<FileInfo, Commit> collectChanges(Repository repository, String commitNumber) {
        return null;
    }

    public List<String> getAddedFiles() {
        return null;
    }

    public void copyFilesToCommitDirectory(Commit commit) {

    }

    public void clearStagedFiles() {

    }

    public List<Commit> getPathFromCommit(Repository repository, String lastCommitNumberInSrc) {
        return null;
    }

    public void copyFromCommitDirs(Map<FileInfo, Commit> changes) {

    }
}
