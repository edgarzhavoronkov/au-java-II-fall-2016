package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;

/**
 * Created by Эдгар on 02.10.2016.
 * Implementation of a {@link Command} interface for Rm
 */
public class RmCmd implements Command {
    /**
     * Overridden execute method for Rm
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments, such as files to remove
     * @return message with number of removed files
     */
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length == 0) {
            return getUsage();
        }

        core.getRepository().removeFiles(args);
        return String.format("Removed %d file(s)", args.length);
    }

    @Override
    public String getUsage() {
        return "Usage: rm $file1 $file2 ...; Removes $file1 $file2 etc.";
    }
}
