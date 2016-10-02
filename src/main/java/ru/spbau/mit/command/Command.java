package ru.spbau.mit.command;

import ru.spbau.mit.environment.Environment;

/**
 * Created by Эдгар on 25.09.2016.
 */
public interface Command {
    String execute(Environment environment, String[] args);
}
