package ru.spbau.mit.command;

import ru.spbau.mit.model.core.VcsCore;
import ru.spbau.mit.exceptions.CommandFailException;

import java.io.File;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class InitCmd implements Command {
    @Override
    public String execute(VcsCore vcs, String[] args) {
        if (args.length != 0) {
            throw new CommandFailException("Init does not need any arguments");
        }

        File currentRepositoryDir = vcs.getFileUtils().getCurrentDirectory().toFile();
        if (currentRepositoryDir.exists()) {
            throw new CommandFailException("You are trying to init existing repository");
        }

        vcs.getFileUtils().mkDir(currentRepositoryDir);
        return "Init new repository";
    }
}
