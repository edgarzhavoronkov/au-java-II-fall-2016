package ru.spbau.mit.command;

import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.Snapshot;
import ru.spbau.mit.model.core.VcsCore;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 02.10.2016.
 */
public class StatusCmd implements Command {
    @Override
    public String execute(VcsCore core, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Status does not takes arguments");
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

            StringBuilder result = new StringBuilder();

            result.append(printList("Added files: ", addedFiles));
            result.append(printList("Removed files: ", removedFiles));
            result.append(printList("Modified files: ", modifiedFiles));
            result.append(printList("Untracked files: ", untrackedFiles));

            return result.toString();

        } catch (IOException e) {
            throw new CommandFailException(e);
        }
    }

    private StringBuilder printList(String listName, List<String> list) {
        StringBuilder res = new StringBuilder();
        if (!list.isEmpty()) {
            res.append(listName);
            res.append("\n");
            list.forEach(res::append);
        }
        return res;
    }
}
