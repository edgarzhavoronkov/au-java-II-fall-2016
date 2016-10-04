package ru.spbau.mit.core;

import lombok.Getter;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.Branch;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;

import java.util.*;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class VcsCore {
    public static final String VCS_FOLDER_NAME = ".repo";
    public static final String DEFAULT_BRANCH_NAME = "def";
    public static final long INIT_COMMIT_NUMBER = 0;

    @Getter
    private Branch currentBranch;
    private long currentCommitNumber;
    private long nextCommitNumber;

    @Getter
    private final Set<Branch> branches;
    private final Map<Long, Commit> commits;
    @Getter
    private final Repository repository;

    private VcsCore() {
        this(System.getProperty("user.dir"));
    }

    private VcsCore(String workingDirectory) {
        this(
                new Branch(
                        DEFAULT_BRANCH_NAME
                        , INIT_COMMIT_NUMBER)
                , INIT_COMMIT_NUMBER
                , new HashSet<>()
                , new HashMap<>()
                , new Repository(workingDirectory)
                , INIT_COMMIT_NUMBER);
    }

    private VcsCore(
            Branch currentBranch
            , long currentCommitNumber
            , Set<Branch> branches
            , Map<Long, Commit> commits
            , Repository repository
            , long nextCommitNumber) {
        this.currentBranch = currentBranch;
        this.currentCommitNumber = currentCommitNumber;
        this.branches = branches;
        this.commits = commits;
        this.repository = repository;
        this.nextCommitNumber = nextCommitNumber;
        branches.add(currentBranch);
    }

    public static VcsCore getInstance() {
        return new VcsCore();
    }

    public void createBranch(String name) throws CoreException {
        Branch branch = new Branch(name, currentCommitNumber);
        if (!branches.contains(branch)) {
            branches.add(branch);
        }
        throw new CoreException(String.format("Branch %s already exists", name));
    }

    public void removeBranch(String name) throws CoreException {
        Branch branch = getBranchByName(name);
        if (branch != null) {
            branches.remove(branch);
        }
        throw new CoreException(String.format("Failed to remove non-existent branch %s", name));
    }

    public void checkoutCommit(long commitNumber) throws CoreException {
        if (commits.get(commitNumber) != null) {
            currentCommitNumber = commitNumber;
            //maybe set current branch's  head commit number to new number?
            currentBranch = null;
            repository.checkoutCommit(currentCommitNumber);
        }
        throw new CoreException(String.format("Failed to checkout non-existing revision %d", commitNumber));
    }

    public void checkoutBranch(String branchName) throws CoreException {
        Branch branch = getBranchByName(branchName);
        if (branch != null) {
            currentBranch = branch;
            currentCommitNumber = branch.getHeadCommitNumber();
            repository.checkoutCommit(currentCommitNumber);
        }
        throw new CoreException(String.format("Failed to checkout non-existing branch %s", branchName));
    }

    public void commit(String message) throws CoreException {
        if (currentBranch == null) {
            throw new CoreException("No branch to commit into");
        }
        long newCommitNumber = addCommit(message);
        repository.saveCommit(newCommitNumber);
    }

    public Commit getCurrentCommit() {
        return getCommitByNumber(currentCommitNumber);
    }

    public Commit getCommitByNumber(long number) {
        return commits.get(number);
    }

    public void merge(String srcBranchName) throws CoreException {
        Branch src = getBranchByName(srcBranchName);
        if (src == null) {
            throw new CoreException(String.format("Can't find branch %s to merge into", srcBranchName));
        }

        long srcHeadNumber = src.getHeadCommitNumber();
        Commit srcCommit = getCommitByNumber(srcHeadNumber);
        Commit dstCommit = getCurrentCommit();

        while (srcCommit.getNumber() != dstCommit.getNumber()) {
            if (srcCommit.getNumber() > dstCommit.getNumber()) {
                srcCommit = getCommitByNumber(srcCommit.getParentCommitNumber());
            } else {
                dstCommit = getCommitByNumber(dstCommit.getParentCommitNumber());
            }
        }
        long baseCommitNumber = srcCommit.getNumber();
        if (srcHeadNumber <= baseCommitNumber) {
            throw new CoreException("Merge failed. Everything is up-to-date");
        }
        repository.merge(srcHeadNumber, currentCommitNumber, srcCommit.getNumber(), nextCommitNumber);
        String mergeMessage = String.format("Merged branch %s into %s", src.getName(), currentBranch.getName());
        addCommit(mergeMessage);
    }

    private long addCommit(String message) {
        long number = nextCommitNumber;
        Commit commit = new Commit(number, message, currentCommitNumber);
        commits.put(number, commit);
        currentCommitNumber = number;
        currentBranch.setHeadCommitNumber(number);
        ++nextCommitNumber;
        return number;
    }

    private Branch getBranchByName(String name) {
        for (Branch branch : branches) {
            if (branch.getName().equals(name)) {
                return branch;
            }
        }
        return null;
    }

}
