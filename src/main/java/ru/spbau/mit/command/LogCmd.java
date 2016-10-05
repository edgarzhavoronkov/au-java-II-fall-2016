package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of a {@link Command} interface for Log
 */
public class LogCmd implements Command {
    /**
     * Overridden execute method for Log
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String}. Must be empty
     * @return Big string with info about all {@link Commit}s
     * in current {@link ru.spbau.mit.model.Branch}
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Log command does not take any arguments");
        }

        StringBuilder result = new StringBuilder();
        Commit commit = core.getCurrentCommit();
        while (commit != null && commit.getNumber() > 0) {
            result.append(commit.getNumber());
            result.append(" : ");
            result.append(commit.getMessage());
            result.append("\n");
            commit = core.getCommitByNumber(commit.getParentCommitNumber());
        }

        return result.toString();
    }
}
