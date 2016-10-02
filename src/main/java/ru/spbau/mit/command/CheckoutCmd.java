package ru.spbau.mit.command;

import ru.spbau.mit.environment.Environment;
import ru.spbau.mit.exceptions.CommandFailException;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of Command interface for Checkout
 * Shitcode warning
 * 02.10 - Still lots of shitcode
 */
public class CheckoutCmd implements Command {
    @Override
    public String execute(Environment environment, String[] args) {
        if (!environment.getVcsCore().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (!environment.getVcsCore().haveUncommittedChanges()) {
            throw new CommandFailException("You have changes to commit! Commit them, or they will probably be lost");
        }

        if (args.length != 2) {
            throw new CommandFailException("Wrong arguments! Provide either -c and commit number or -b and branch name");
        }


        switch (args[0]) {
            case "-c":
                environment.getRepository().checkoutCommit(args[1]);

                //TODO: boilerplate about collecting files

                return String.format("Checked out commit %s", args[1]);
            case "-b":
                environment.getRepository().checkoutBranch(args[1]);

                //TODO: boilerplate about collecting files

                return String.format("Checked out branch %s", args[1]);
            default:
                throw new CommandFailException("Wrong key! Use either -c to checkout to certain commit or -b to checkout to a particular commit");
        }
    }
}
