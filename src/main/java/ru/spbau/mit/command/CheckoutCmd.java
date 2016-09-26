package ru.spbau.mit.command;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.commit.Commit;
import ru.spbau.mit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of Command interface for Commit
 * Shitcode warning
 */
public class CheckoutCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        if (!repository.getAddedFiles().isEmpty()) {
            return "You have changes to commit! Commit them, or they will probably be lost";
        }
        String commitNumber = "";
        String branchName = "";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-b")) {
                if (i + 1 < args.length && !args[i+1].equals("-r")) {
                    branchName = args[i + 1];
                } else {
                    return "Branch name not provided";
                }
            }
            if (args[i].equals("-r")) {
                if (i + 1 < args.length && !args[i+1].equals("-b")) {
                    commitNumber = args[i + 1];
                } else {
                    return "Revision number not provided";
                }
            }
        }

        if (branchName.isEmpty() && commitNumber.isEmpty()) {
            return "I have ho idea what to checkout. Specify either branch name or revision number";
        } else if (!branchName.isEmpty() && commitNumber.isEmpty()) {
            Branch targetBranch = repository.getBranches().get(branchName);
            if (targetBranch != null) {
                if (!targetBranch.getCommits().isEmpty()) {
                    Commit targetCommit = targetBranch.getCommits().get(0);
                    return updateRepository(repository, targetBranch, targetCommit);
                } else {
                    repository.setCurrentBranch(targetBranch);
                    return String.format("Checked out empty branch %s", branchName);
                }
            } else {
                return String.format("No branch %s found!", branchName);
            }
        } else if (branchName.isEmpty() && !commitNumber.isEmpty()) {
            Commit targetCommit = null;
            Branch targetBranch = null;
            for (String key : repository.getBranches().keySet()) {
                for (Commit commit : repository.getBranches().get(key).getCommits()) {
                    if (commit.getNumber().equals(commitNumber)) {
                        targetCommit = commit;
                        targetBranch = repository.getBranches().get(key);
                        break;
                    }
                }
            }
            if (targetCommit != null && targetBranch != null) {
                return updateRepository(repository, targetBranch, targetCommit);
            } else {
                return String.format("Failed to find commit %s in all the branches", commitNumber);
            }
        } else {
            Branch targetBranch = repository.getBranches().get(branchName);
            if (targetBranch != null) {
                Commit targetCommit = null;
                for (Commit commit : targetBranch.getCommits()) {
                    if (commit.getNumber().equals(commitNumber)) {
                        targetCommit = commit;
                        break;
                    }
                }
                if (targetCommit != null) {
                    return updateRepository(repository, targetBranch, targetCommit);
                } else {
                    return String.format("No commit %s found", commitNumber);
                }
            } else {
                return String.format("No branch %s found", branchName);
            }
        }
    }

    private String updateRepository(Repository repository, Branch targetBranch, Commit targetCommit) {
        String targetBranchName = targetBranch.getName();
        String targetCommitNumber = targetCommit.getNumber();
        String pathToFileInCommit = String.format("%s/.repo/branches/%s/%s/", repository.getRepoPath(), targetBranchName, targetCommitNumber);
        for (String filename : targetCommit.getFiles()) {
            Path target = Paths.get(repository.getRepoPath() + filename);
            Path source = Paths.get(pathToFileInCommit + filename);
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                return "Checkout failed for no reasons";
            }
        }
        repository.setCurrentBranch(targetBranch);
        return String.format("Checked out branch %s at commit %s", targetBranchName, targetCommitNumber);
    }
}
