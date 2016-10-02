package ru.spbau.mit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
@EqualsAndHashCode
public class Branch implements Serializable {
    @Getter
    private final String name;
    @Getter
    private final List<Commit> commits;

    public Branch(String name) {
        this.name = name;
        this.commits = new ArrayList<>();
    }

    public Commit addCommit(
            String message
            , String commitNumber
            , List<FileInfo> addedFiles
            , List<FileInfo> removedFiles) {
        Commit commit = new Commit(name, commitNumber, null, message, addedFiles, removedFiles);
        commits.add(commit);
        return commit;
    }
}
