package ru.spbau.mit.command;

import ru.spbau.mit.repository.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class AddCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        List<String> files = new ArrayList<>();
        for (String arg : args) {
            files.add(arg);
        }
        repository.setAddedFiles(files);
        return String.format("Added %d files", files.size());
    }
}
