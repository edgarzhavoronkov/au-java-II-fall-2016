package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;
import ru.spbau.mit.model.Branch;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.exceptions.CommandFailException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class LogCmd implements Command {
    @Override
    public String execute(VcsCore vcs, String[] args) {
        if (vcs.getVcsCore().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length != 0) {
            throw new CommandFailException("Log does not need any arguments");
        }

        Branch currentBranch = vcs.getRepository().getBranchByName(vcs.getRepository().getCurrentBranchName());

        StringBuilder result = new StringBuilder();
        List<Commit> sorted = currentBranch.getCommits()
                .stream()
                .sorted((c1, c2) -> c1.getCommitNumber().compareTo(c2.getCommitNumber()))
                .collect(Collectors.toList());
        for (Commit commit : sorted) {
            result.append(commit.getCommitNumber())
                    .append(": ")
                    .append(commit.getMessage())
                    .append("\n");
        }
        return result.toString();
    }
}
