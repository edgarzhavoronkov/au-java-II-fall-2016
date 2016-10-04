package ru.spbau.mit.runner;

import ru.spbau.mit.command.Command;
import ru.spbau.mit.command.CommandProvider;
import ru.spbau.mit.core.VcsCore;
import ru.spbau.mit.exceptions.CommandFailException;

/**
 * Created by Эдгар on 30.09.2016.
 * Implementation of {@link Runner} interface
 * Runs commands which are given from the console
 */
public class ConsoleRunner implements Runner {
    @Override
    public void run(String[] args, VcsCore vcs) {
        if (args.length == 0) {
            System.out.println("No command specified!");
            System.exit(-1);
        } else {
            Command cmd = CommandProvider.forName(args[0]);
            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, args.length - 1);
            try {
                String output = cmd.execute(vcs, arguments);
                System.out.println(output);
            } catch (CommandFailException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
