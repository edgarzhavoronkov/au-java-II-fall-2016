package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.BranchCreateFailException;
import ru.spbau.mit.exceptions.BranchDeleteFailException;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of {@link Command} interface for Branch command
 */
public class BranchCmd implements Command {
    /**
     * Overridden execute method for Branch
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments
     * @return message whether branch is successfully created or deleted
     * @throws CommandFailException in general case or it's extending classes if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 2) {
            return getUsage();
        }
        switch (args[0]) {
            case "-c" :
                try {
                    core.createBranch(args[1]);
                    core.checkoutBranch(args[1]);
                    return String.format("Created branch %s", args[1]);
                } catch (CoreException e) {
                    throw new BranchCreateFailException(e);
                }
            case "-r" :
                try {
                    core.removeBranch(args[1]);
                    return String.format("Branch %s was removed!", args[1]);
                } catch (CoreException e) {
                    throw new BranchDeleteFailException(e);
                }
            default :
                return getUsage();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: `branch -c $branch_name` to create branch or `branch -d $branch_name` to delete branch";
    }
}
