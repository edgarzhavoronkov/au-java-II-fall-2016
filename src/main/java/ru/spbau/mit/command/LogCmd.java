package ru.spbau.mit.command;

import ru.spbau.mit.commit.Commit;
import ru.spbau.mit.repository.Repository;

import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class LogCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        List<Commit> commits = repository.getCurrentBranch().getCommits();
        commits.sort((c1, c2) -> c2.getNumber().compareTo(c1.getNumber()));
        StringBuilder result = new StringBuilder();
        for (Commit commit : commits) {
            result.append(commit.getNumber());
            result.append(" ");
            result.append(commit.getMessage());
            result.append("\n");
        }
        return result.toString();
    }
}
