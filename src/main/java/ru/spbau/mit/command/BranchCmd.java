package ru.spbau.mit.command;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.repository.Repository;

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
                    return String.format("Branch %s was created", branchName);
                }
            }
            if (args[i].equals("-d")) {
                if (i + 1 < args.length ) {
                    branchName = args[i + 1];
                    Branch removed = repository.getBranches().remove(branchName);
                    if (removed != null) {
                        return String.format("Branch %s was successfully removed", branchName);
                    } else {
                        return String.format("Failed to remove branch %s since it is not present", branchName);
                    }
                }
            }
        }
        return "Branch name can't be null. Please provide name of a branch";
    }
}
