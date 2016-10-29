package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 * Implementation of a {@link Command} interface for Reset
 */
public class RmCmd implements Command {
    /**
     * Overridden execute method for Rm
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments, such as files to remove
     * @return message with number of removed files
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 0) {
            throw new CommandFailException("Wrong number of arguments! Rm takes list of filenames to remove");
        }

        core.getRepository().removeFiles(args);
        return String.format("Removed %d file(s)", args.length);
    }
}
