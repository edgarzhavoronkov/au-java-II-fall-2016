package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 */
public class RmCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Wrong number of arguments! Rm takes list of filenames to remove");
        }

        core.getRepository().removeFiles(args);
        return String.format("Removed %d file(s)", args.length);
    }
}
