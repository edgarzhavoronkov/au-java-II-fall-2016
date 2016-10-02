package ru.spbau.mit.command;

import ru.spbau.mit.environment.Environment;
import ru.spbau.mit.exceptions.CommandFailException;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class AddCmd implements Command {
    @Override
    public String execute(Environment environment, String[] args) {
        if (!environment.getRepoUtils().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length == 0) {
            throw new CommandFailException("I have no files to add");
        }

        Path currentDirectory = environment.getFileUtils().getCurrentDirectory();

        for (String arg : args) {
            if (!new File(currentDirectory.toString(), arg).exists()) {
                throw new CommandFailException(String.format("No file %s found", arg));
            }
        }

        List<String> addedFileNames = Arrays.asList(args);
        environment.getRepoUtils().addToStagedFiles(addedFileNames);


        return String.format("Added %d file(s)", addedFileNames.size());
    }
}
