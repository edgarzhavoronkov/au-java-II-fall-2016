package ru.spbau.mit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for Branch in VCS
 * Knows it's name and all commits in done in that branch
 * Identified by name
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
            , String parentCommitNumber
            , List<FileInfo> addedFiles
            , List<FileInfo> removedFiles) {
        Commit commit = new Commit(
                name
                , commitNumber
                , parentCommitNumber
                , message
                , addedFiles
                , removedFiles
        );
        commits.add(commit);
        return commit;
    }
}
