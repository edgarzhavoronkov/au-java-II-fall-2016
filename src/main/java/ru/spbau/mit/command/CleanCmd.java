package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 * Implementation of a {@link Command} interface for Clean
 */
public class CleanCmd implements Command {
    /**
     * Overridden execute method for Clean
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments
     * @return message whether clean was successful
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Clean does not take any arguments");
        }

        core.getRepository().clean();
        return "Successfully cleaned";
    }
}
