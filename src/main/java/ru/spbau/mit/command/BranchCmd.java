package ru.spbau.mit.command;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.repository.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class BranchCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        String branchName;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-c")) {
                if (i + 1 < args.length ) {
                    branchName = args[i + 1];
                    repository.getBranches().put(branchName, new Branch(branchName));
                    Path pathToBranch = Paths.get(
                            String.format(
                                    "%s/.repo/branches/%s"
                                    , repository.getRepoPath()
                                    , branchName
                            )
                    );
                    try {
                        Files.createDirectory(pathToBranch);
                        repository.setCurrentBranch(repository.getBranches().get(branchName));
                        return String.format("Branch %s was created", branchName);
                    } catch (IOException e) {
                        return String.format("Failed to create branch %s due to I/O error", branchName);
                    }
                }
            }
            if (args[i].equals("-d")) {
                if (i + 1 < args.length ) {
                    branchName = args[i + 1];
                    if (!repository.getCurrentBranch().getName().equals(branchName)) {
                        Branch removed = repository.getBranches().remove(branchName);
                        if (removed != null) {
                            Path pathToBranch = Paths.get(
                                    String.format(
                                            "%s/.repo/branches/%s"
                                            , repository.getRepoPath()
                                            , branchName
                                    )
                            );
                            try {
                                Files.delete(pathToBranch);
                            } catch (IOException e) {
                                return String.format("Failed to remove branch %s due to I/O error", branchName);
                            }
                            return String.format("Branch %s was successfully removed", branchName);
                        } else {
                            return String.format("Failed to remove branch %s since it is not present", branchName);
                        }
                    } else {
                        return "Cannot remove current branch. Switch to another one and try again";
                    }
                }
            }
        }
        return "Branch name can't be null. Please provide name of a branch";
    }
}
