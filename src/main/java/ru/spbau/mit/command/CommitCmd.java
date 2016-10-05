package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class CommitCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 2) {
            throw new CommandFailException("Wrong number of arguments!");
        }

        switch (args[0]) {
            case "-m" :
                try {
                    core.commit(args[1]);
                    return "Commit created!";
                } catch (CoreException e) {
                    throw new CommandFailException(e);
                }
            default :
                throw new CommandFailException("Wrong key! Usage: `commit -m message`");
        }
    }
}
