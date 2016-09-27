package ru.spbau.mit.command;

import ru.spbau.mit.repository.Repository;

/**
 * Created by Эдгар on 25.09.2016.
 */
public interface Command {
    String execute(Repository repository, String[] args);
}
