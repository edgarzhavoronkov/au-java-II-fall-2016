package ru.spbau.mit.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Эдгар on 25.09.2016.
 * Simple factory for commands
 */
public class CommandProvider {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("add", new AddCmd());
        commands.put("branch", new BranchCmd());
        commands.put("checkout", new CheckoutCmd());
        commands.put("commit", new CommitCmd());
        commands.put("clean", new CleanCmd());
        commands.put("log", new LogCmd());
        commands.put("merge", new MergeCmd());
        commands.put("reset", new ResetCmd());
        commands.put("rm", new RmCmd());
        commands.put("status", new StatusCmd());
    }

    public static Command forName(String name) {
        return commands.get(name);
    }

}
