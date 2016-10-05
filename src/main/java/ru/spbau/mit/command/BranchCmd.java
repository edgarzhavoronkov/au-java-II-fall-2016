package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class BranchCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 2) {
            throw new CommandFailException("Wrong number of arguments!");
        }
        switch (args[0]) {
            case "-c" :
                try {
                    core.createBranch(args[1]);
                    core.checkoutBranch(args[1]);
                    return String.format("Created branch%s", args[1]);
                } catch (CoreException e) {
                    throw new CommandFailException(e);
                }
            case "-d" :
                try {
                    core.removeBranch(args[1]);
                    return String.format("Branch %s was removed!", args[1]);
                } catch (CoreException e) {
                    throw new CommandFailException(e);
                }
            default :
                throw new CommandFailException("Wrong key! Usage: `branch -c $branch_name` to create " +
                        "branch or `branch -d $branch_name` to delete branch");
        }
    }
}
