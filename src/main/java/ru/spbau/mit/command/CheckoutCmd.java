package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CheckoutBranchFailException;
import ru.spbau.mit.exceptions.CheckoutCommitFailException;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of a {@link Command} interface for Checkout
 */
public class CheckoutCmd implements Command {
    /**
     * Overridden execute method for Checkout
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments
     * @return message whether we checkout to a branch or to a commit
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 2) {
            throw new CommandFailException("Wrong number of arguments");
        }

        switch (args[0]) {
            case "-b" :
                try {
                    core.checkoutBranch(args[1]);
                    return String.format("Checked out branch %s", args[1]);
                } catch (CoreException e) {
                    throw new CheckoutBranchFailException(e);
                }

            case "-c" :
                try {
                    core.checkoutCommit(Long.parseLong(args[1]));
                    return String.format("Checked out commit number %s", args[1]);
                } catch (CoreException e) {
                    throw new CheckoutCommitFailException(e);
                }

            default :
                throw new CommandFailException("Usage: checkout -b $branch_name" +
                        " to checkout particular branch or `checkout " +
                        "-c $commit_number` to checkout to a particular commit");
        }
    }
}
