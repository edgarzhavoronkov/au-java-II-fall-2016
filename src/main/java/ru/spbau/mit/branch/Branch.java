package ru.spbau.mit.branch;

import ru.spbau.mit.commit.Commit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class Branch {
    private final String branchName;
    private final List<Commit> commits;


    public Branch(String branchName) {
        this.branchName = branchName;
        this.commits = new ArrayList<>();
    }

    public String getName() {
        return branchName;
    }

    public void addCommit(Commit commit) {
        commits.add(commit);
    }

    public List<Commit> getCommits() {
        return commits;
    }
}
