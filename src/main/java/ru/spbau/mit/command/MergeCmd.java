package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.exceptions.MergeFailedException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of a {@link Command} interface for Log
 */
public class MergeCmd implements Command {
    /**
     * Overridden execute method for Merge
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments, such as
     * {@link ru.spbau.mit.model.Branch} which you merge into
     *             current {@link ru.spbau.mit.model.Branch}
     *
     * @return message whether merge-commit was created
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 1) {
            return getUsage();
        }

        try {
            core.merge(args[0]);
            return String.format("Merged branch %s into %s", args[0], core.getCurrentBranch().getName());
        } catch (CoreException e) {
            throw new MergeFailedException(e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "Usage: merge $branch_name. Merges branch $branch_name into current branch";
    }
}
