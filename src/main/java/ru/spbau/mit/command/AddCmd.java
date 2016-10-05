package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of {@link Command} interface for Add command
 */
public class AddCmd implements Command {
    /**
     * Overridden execute method for Add
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with filenames to add
     * @return Message with number of added files
     */
    @Override
    public String execute(VcsCore core, String[] args) {
        core.getRepository().addFiles(args);
        return String.format("Added %d file(s)", args.length);
    }
}
