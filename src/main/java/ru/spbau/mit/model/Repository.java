package ru.spbau.mit.model;

import lombok.Getter;
import ru.spbau.mit.util.CommitNameProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for serializable repository
 * internal state is presented by list of branches,
 * name of the current branch and number of current commit(like HEAD)
 */
public class Repository implements Serializable {
    private final List<Branch> branches = new ArrayList<>();
    
    private transient Map<String, Branch> allBranches;
    private transient Map<String, Commit> allCommits;
    
    @Getter
    private String currentBranchName;
    
    @Getter
    private String currentCommitNumber;

    public Repository() {
        addNewBranch("def");
    }

    public Branch addNewBranch(String name) {
        Branch newBranch = new Branch(name);
        if (allBranches.get(name) != null) {
            throw new RuntimeException(String.format("Branch %s already exists", name));
        }
        branches.add(newBranch);
        allBranches.put(name, newBranch);
        checkoutBranch(name);
        return newBranch;
    }

    public Branch removeBranch(String branchName) {
        Branch removed = allBranches.get(branchName);
        if (removed != null) {
            for (int i = 0; i < branches.size(); i++) {
                if (branches.get(i).getName().equals(branchName)) {
                    branches.remove(i);
                    allBranches.remove(branchName);
                    return removed;
                }
            }
        }
        throw new RuntimeException(String.format("Branch %s was not found", branchName));
    }

    public Commit addNewCommit(String message, List<FileInfo> addedFiles, List<FileInfo> removedFiles) {
        Branch currentBranch = getAllBranches().get(currentBranchName);
        String newCommitNumber = CommitNameProvider.getNewName();
        Commit commit = currentBranch.addCommit(
                message
                , newCommitNumber
                , currentCommitNumber
                , addedFiles
                , removedFiles);
        checkoutCommit(commit.getCommitNumber());
        return commit;
    }

    public void checkoutCommit(String commitNumber) {
        Commit commit = getCommitByNumber(commitNumber);

        if (commit != null) {
            currentCommitNumber = commitNumber;
            currentBranchName = commit.getBranchName();
            return;
        }
        throw new RuntimeException(
                String.format("No commit %s found", commitNumber)
        );
    }

    public void checkoutBranch(String branchName) {
        Branch branch = getBranchByName(branchName);

        if (branch != null) {
            currentCommitNumber = branch.getCommits().get(0).getCommitNumber();
            currentBranchName = branch.getName();
            return;
        }

        throw new RuntimeException(
                String.format("No branch %s found", branchName)
        );
    }

    public Branch getBranchByName(String branchName) {
        return getAllBranches().get(branchName);
    }

    public Commit getCommitByNumber(String commitNumber) {
        return getAllCommits().get(commitNumber);
    }

    private Map<String, Commit> getAllCommits() {
        if (allCommits == null) {
            allCommits = new HashMap<>();
            for (Branch branch : branches) {
                for (Commit commit : branch.getCommits()) {
                    allCommits.put(commit.getCommitNumber(), commit);
                }
            }
        }
        return allCommits;
    }


    private Map<String, Branch> getAllBranches() {
        if (allBranches == null) {
            allBranches = new HashMap<>();
            for (Branch branch : branches) {
                allBranches.put(branch.getName(), branch);
            }
        }
        return allBranches;
    }
}
