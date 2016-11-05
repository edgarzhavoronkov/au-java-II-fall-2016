package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * General interface for all commands
 */
public interface Command {
    /**
     * Only one method for executing commands
     * @param core {@link VcsCore} which is supposed to do all the job
     * @param args Array of {@link String} as arguments, keys, etc
     * @return result of execution
     * @throws CommandFailException is something gone wrong
     */
    String execute(VcsCore core, String[] args) throws CommandFailException;

    String getUsage();
}
