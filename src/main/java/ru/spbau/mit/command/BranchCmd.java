package ru.spbau.mit.command;

import ru.spbau.mit.environment.Environment;
import ru.spbau.mit.model.Branch;
import ru.spbau.mit.exceptions.CommandFailException;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class BranchCmd implements Command {
    @Override
    public String execute(Environment environment, String[] args) {
        if (!environment.getVcsCore().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length != 2) {
            throw new CommandFailException("Wrong arguments! Use -c and a branch name to create branch with provided name or -d and branch name to remove branch with given name");
        }

        if (args[0].equals("-c")) {
            Branch branch = environment.getRepository().addNewBranch(args[1]);
            return String.format("Branch %s was successfully created", branch.getName());
        } else if (args[0].equals("-d")) {
            Branch branch = environment.getRepository().removeBranch(args[1]);
            return String.format("Branch %s was successfully removed", branch.getName());
        } else {
            throw new CommandFailException("I can't figure out what do you want me to do =(");
        }
    }
}
