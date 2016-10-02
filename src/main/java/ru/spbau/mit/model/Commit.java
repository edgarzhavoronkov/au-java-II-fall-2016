package ru.spbau.mit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for commit in VCS
 * Knows about {@link Branch}, parent commit
 * Also knows information about added and removed files
 */
@EqualsAndHashCode
public class Commit implements Serializable {
    @Getter
    private final String branchName;
    @Getter
    private final String commitNumber;
    @Getter
    private final String parentCommitNumber;
    @Getter
    private final String message;

    private final List<FileInfo> addedFiles;

    private final List<FileInfo> removedFiles;

    public Commit(
            String branchName
            , String commitNumber
            , String parentCommitNumber, String message
            , List<FileInfo> addedFiles
            , List<FileInfo> removedFiles) {

        this.branchName = branchName;
        this.commitNumber = commitNumber;
        this.parentCommitNumber = parentCommitNumber;
        this.message = message;
        this.addedFiles = addedFiles;
        this.removedFiles = removedFiles;
    }

    public List<FileInfo> getAddedFiles() {
        return Collections.unmodifiableList(addedFiles);
    }

    public List<FileInfo> getRemovedFiles() {
        return Collections.unmodifiableList(removedFiles);
    }
}
