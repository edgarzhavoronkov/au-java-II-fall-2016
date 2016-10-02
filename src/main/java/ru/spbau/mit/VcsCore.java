package ru.spbau.mit;

import ru.spbau.mit.model.FileInfo;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.util.FileUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 01.10.2016.
 * Core class for VCS which does all the job
 * with filesystem using corresponding wrapper
 */
public class VcsCore {
    private final FileUtils fileUtils;

    public VcsCore(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public Repository loadRepositoryFromDisk() {
        return null;
    }

    public void saveRepositoryToDisk(Repository repository) {

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
