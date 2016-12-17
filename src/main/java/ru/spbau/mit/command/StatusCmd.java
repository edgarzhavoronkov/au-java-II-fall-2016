package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.RepositoryException;
import ru.spbau.mit.exceptions.StatusFailException;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.Snapshot;
import ru.spbau.mit.model.core.VcsCore;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 02.10.2016.
 * Implementation of a {@link Command} interface for Reset
 */
public class StatusCmd implements Command {
    /**
     * Overridden execute method for Status
     * @param core {@link VcsCore} which does all the job
     * @param args Array of {@link String} with arguments. Must be empty
     * @return message with information about added, removed, modified and untracked files
     * @throws CommandFailException if something went wrong
     */
    @Override
    public String execute(VcsCore core, String[] args) throws CommandFailException {
        if (args.length != 0) {
            return getUsage();
        }

        Repository repository = core.getRepository();
        try {
            Snapshot prev = repository.getSnapshotByCommitNumber(core.getCurrentCommit().getParentCommitNumber());
            Snapshot curr = repository.getCurrentSnapshot();
            Set<String> trackedFiles = repository.getTrackedFiles();

            List<String> addedFiles = trackedFiles.stream()
                    .filter(file -> curr.contains(file) && !prev.contains(file))
                    .collect(Collectors.toList());

            List<String> removedFiles = prev.filenameSet().stream()
                    .filter(file -> !curr.contains(file))
                    .collect(Collectors.toList());

            List<String> modifiedFiles = prev.filenameSet().stream()
                    .filter(file -> curr.contains(file) && !curr.getFileHash(file).equals(prev.getFileHash(file)))
                    .collect(Collectors.toList());

            List<String> untrackedFiles = curr.filenameSet().stream()
                    .filter(file -> !trackedFiles.contains(file))
                    .collect(Collectors.toList());

            return new StringBuilder()
                    .append(printList("Added files: ", addedFiles))
                    .append(printList("Removed files: ", removedFiles))
                    .append(printList("Modified files: ", modifiedFiles))
                    .append(printList("Untracked files: ", untrackedFiles))
                    .toString();

        } catch (RepositoryException e) {
            throw new StatusFailException(e);
        }
    }

    @Override
    public String getUsage() {
        return "Usage: status. Does not take any arguments";
    }


    private StringBuilder printList(String listName, List<String> list) {
        StringBuilder res = new StringBuilder();
        if (!list.isEmpty()) {
            res.append(listName);
            res.append("\n");
            list.forEach((name) -> res.append('\t').append(name).append('\n'));
        }
        return res;
    }
}
