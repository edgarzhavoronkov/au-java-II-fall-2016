package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 */
public class CleanCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Clean does not take any arguments");
        }

        core.getRepository().clean();
        return "Successfully cleaned";
    }
}
