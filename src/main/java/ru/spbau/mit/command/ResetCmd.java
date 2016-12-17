package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.RepositoryException;
import ru.spbau.mit.exceptions.ResetFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 * Implementation of a {@link Command} interface for Reset
 */
public class ResetCmd implements Command {
    /**
     * Overridden execute method for Reset
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments, such as name of file to reset
     * @return message whether file was removed from index in current {@link ru.spbau.mit.model.Commit}
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 1) {
            return getUsage();
        }

        try {
            core.getRepository().resetFile(args[0], core.getCurrentCommit().getNumber());
            return String.format("Removed file %s from index", args[0]);
        } catch (RepositoryException e) {
            throw new ResetFailException(e);
        }
    }

    @Override
    public String getUsage() {
        return "Usage: reset $file. Removes $file from index";
    }
}
