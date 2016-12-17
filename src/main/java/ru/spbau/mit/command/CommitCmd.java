package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CommitFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of a {@link Command} interface for Commit
 */
public class CommitCmd implements Command {
    /**
     * Overridden execute method for Commit
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments, such as message
     * @return message whether commit was created
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 2) {
            return getUsage();
        }

        switch (args[0]) {
            case "-m" :
                try {
                    core.commit(args[1]);
                    return "Commit created!";
                } catch (CoreException e) {
                    throw new CommitFailException(e);
                }
            default :
                return getUsage();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: `commit -m message`";
    }
}
