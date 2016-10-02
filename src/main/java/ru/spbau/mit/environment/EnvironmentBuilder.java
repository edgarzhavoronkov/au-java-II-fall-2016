package ru.spbau.mit.environment;

import ru.spbau.mit.model.Repository;
import ru.spbau.mit.util.FileUtils;
import ru.spbau.mit.VcsCore;

import java.io.File;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class EnvironmentBuilder {
    public static Environment init() {
        return init(new File("."));
    }

    public static Environment init(File repositoryDir) {
        FileUtils fileUtils = new FileUtils(repositoryDir);
        VcsCore vcsCore = new VcsCore(fileUtils);
        Repository repository = vcsCore.loadRepositoryFromDisk();
        return new Environment(repository, fileUtils, vcsCore);
    }
}
