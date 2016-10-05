package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

import java.io.IOException;

/**
 * Created by Эдгар on 02.10.2016.
 */
public class ResetCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 1) {
            throw new CommandFailException("Wrong number of arguments!");
        }

        try {
            core.getRepository().resetFile(args[0], core.getCurrentCommit().getNumber());
            return String.format("Removed file %s from index", args[0]);
        } catch (IOException e) {
            throw new CommandFailException(e);
        }
    }
}
