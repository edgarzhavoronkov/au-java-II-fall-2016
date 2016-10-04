package ru.spbau.mit.command;

import ru.spbau.mit.core.VcsCore;

/**
 * Created by Эдгар on 25.09.2016.
 */
public interface Command {
    String execute(VcsCore vcs, String[] args);
}
