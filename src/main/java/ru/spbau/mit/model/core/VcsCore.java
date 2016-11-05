package ru.spbau.mit.model.core;

import lombok.Getter;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.exceptions.RepositoryException;
import ru.spbau.mit.model.Branch;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;

import java.io.IOException;
import java.util.*;

/**
 * Created by Эдгар on 01.10.2016.
 * Core class for VCS. Manipulates with filesystem and
 * provides API for {@link ru.spbau.mit.command.Command}
 * interface implementors
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

    public VcsCore() {
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

    /**
     * Creates new branch with given name
     * @param name branch name
     * @throws CoreException if branch with given name is already present
     */
    public void createBranch(String name) throws CoreException {
        Branch branch = new Branch(name, currentCommitNumber);
        if (!branches.contains(branch)) {
            branches.add(branch);
        }
        throw new CoreException(String.format("Branch %s already exists", name));
    }

    /**
     * Removes branch with given name
     * @param name branch name
     * @throws CoreException if branch with given name was not found
     */
    public void removeBranch(String name) throws CoreException {
        Branch branch = getBranchByName(name);
        if (branch != null) {
            branches.remove(branch);
        }
        throw new CoreException(String.format("Failed to remove non-existent branch %s", name));
    }

    /**
     * Checkouts commit by number
     * @param commitNumber number of commit to checkout
     * @throws CoreException if I/O failure or if no such commit is present
     */
    public void checkoutCommit(long commitNumber) throws CoreException {
        if (commits.get(commitNumber) != null) {
            currentCommitNumber = commitNumber;
            //common use case is to create a new branch right after checkout to commit
            currentBranch = null;
            try {
                repository.checkoutCommit(currentCommitNumber);
            } catch (RepositoryException e) {
                throw new CoreException("Failed to checkout due to I/O failure!");
            }
        }
        throw new CoreException(String.format("Failed to checkout non-existing revision %d", commitNumber));
    }

    /**
     * Checkouts branch by it's name
     * @param branchName name of branch to checkout
     * @throws CoreException if I/O failed or if no branch with given name is present
     */
    public void checkoutBranch(String branchName) throws CoreException {
        Branch branch = getBranchByName(branchName);
        if (branch != null) {
            currentBranch = branch;
            currentCommitNumber = branch.getHeadCommitNumber();
            try {
                repository.checkoutCommit(currentCommitNumber);
            } catch (RepositoryException e) {
                throw new CoreException("Failed to checkout due to I/O failure!");
            }
        }
        throw new CoreException(String.format("Failed to checkout non-existing branch %s", branchName));
    }

    /**
     * performs commit with given message
     * @param message {@link String} with message
     * @throws CoreException if I/O failed or current branch is detached
     */
    public void commit(String message) throws CoreException {
        if (currentBranch == null) {
            throw new CoreException("No branch to commit into");
        }
        long newCommitNumber = addCommit(message);
        try {
            repository.saveCommit(newCommitNumber);
        } catch (RepositoryException e) {
            throw new CoreException("Failed to commit due to I/O failure!");
        }
    }

    /**
     * gets current commit(HEAD)
     * @return latest commit
     */
    public Commit getCurrentCommit() {
        return getCommitByNumber(currentCommitNumber);
    }

    /**
     * gets commit by its' number
     * @return Commit with given number
     */
    public Commit getCommitByNumber(long number) {
        return commits.get(number);
    }

    /**
     * searches for two commits and performs three-way merge from their ancestor
     * @param srcBranchName branch name which we want to merge in current branch
     * @throws CoreException if given branch was not found or I/O problem ocured
     */
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
        try {
            repository.merge(srcHeadNumber, currentCommitNumber, srcCommit.getNumber(), nextCommitNumber);
            String mergeMessage = String.format("Merged branch %s into %s", src.getName(), currentBranch.getName());
            addCommit(mergeMessage);
        } catch (RepositoryException e) {
            throw new CoreException(e);
        }
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
