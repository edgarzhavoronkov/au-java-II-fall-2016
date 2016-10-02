package ru.spbau.mit.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.util.FileUtils;
import ru.spbau.mit.VcsCore;

/**
 * Created by Эдгар on 30.09.2016.
 * Environment which knows about repository, filesystem wrapper and core utilities
 * instance of environment can be built using {@link EnvironmentBuilder}
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Environment {
    private Repository repository;
    private FileUtils fileUtils;
    private VcsCore vcsCore;
}
