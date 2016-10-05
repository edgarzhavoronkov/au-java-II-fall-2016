package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class AddCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        core.getRepository().addFiles(args);
        return "Added %d file(s)";
    }
}
