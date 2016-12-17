package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.AddFailException;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

import java.io.FileNotFoundException;

/**
 * Created by Эдгар on 25.09.2016.
 * Implementation of {@link Command} interface for Add command
 */
public class AddCmd implements Command {
    /**
     * Overridden execute method for Add
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with filenames to add
     * @return Message with number of added files
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        for (String filepath : args) {
            if (!core.getRepository().isFileInRepo(filepath)) {
                throw new AddFailException(String.format("File %s lies out of vcs root folder", filepath));
            }
        }
        try {
            core.getRepository().addFiles(args);
        } catch (FileNotFoundException e) {
            throw new AddFailException(e);
        }
        return String.format("Added %d file(s)", args.length);
    }

    @Override
    public String getUsage() {
        return "Usage: add $file1 $file2 ...";
    }
}
