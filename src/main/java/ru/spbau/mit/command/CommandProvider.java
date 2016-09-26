package ru.spbau.mit.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class CommandProvider {
    private static final Map<String, Command> cmds = new HashMap<>();

    static {
        cmds.put("add", new AddCmd());
        cmds.put("branch", new BranchCmd());
        cmds.put("commit", new CommitCmd());
        cmds.put("checkout", new CheckoutCmd());
        cmds.put("log", new LogCmd());
        cmds.put("merge", new MergeCmd());
    }

    public static Command forName(String name) {
        return cmds.get(name);
    }

}
