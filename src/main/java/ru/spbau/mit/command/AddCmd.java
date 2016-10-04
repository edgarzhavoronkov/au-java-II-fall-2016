package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;
import ru.spbau.mit.exceptions.CommandFailException;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class AddCmd implements Command {
    @Override
    public String execute(VcsCore vcs, String[] args) {
        if (!vcs.getVcsCore().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length == 0) {
            throw new CommandFailException("I have no files to add");
        }

        Path currentDirectory = vcs.getFileUtils().getCurrentDirectory();

        for (String arg : args) {
            if (!new File(currentDirectory.toString(), arg).exists()) {
                throw new CommandFailException(String.format("No file %s found", arg));
            }
        }

        List<String> addedFileNames = Arrays.asList(args);
        vcs.getVcsCore().addToStagedFiles(addedFileNames);

        return String.format("Added %d file(s)", addedFileNames.size());
    }
}
