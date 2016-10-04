package ru.spbau.mit;

import ru.spbau.mit.runner.ConsoleRunner;
import ru.spbau.mit.runner.Runner;

/**
 * Created by Edgar on 25.09.2016.
 * Main class for vcs
 * Takes command and its' arguments as command-line arguments
 * It's more convenient since JVM parses input for me
 * Also, it doesn't split input by spaces if they are in double quotes
 */
public class Main {
    public static void main(String[] args) {
        VCS vcs = VCS.getInstance();
        Runner runner = new ConsoleRunner();
        runner.run(args, vcs);
    }
}
