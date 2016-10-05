package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class MergeCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 1) {
            throw new CommandFailException("Wrong number of arguments!");
        }

        try {
            core.merge(args[0]);
            return String.format("Merged branch %s into %s", args[0], core.getCurrentBranch().getName());
        } catch (CoreException e) {
            throw new CommandFailException(e);
        }
    }
}
