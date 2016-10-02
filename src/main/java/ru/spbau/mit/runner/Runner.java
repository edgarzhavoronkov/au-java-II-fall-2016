package ru.spbau.mit.runner;

import ru.spbau.mit.environment.Environment;

/**
 * Created by Эдгар on 30.09.2016.
 */
public interface Runner {
    void run(String[] args, Environment environment);
}
