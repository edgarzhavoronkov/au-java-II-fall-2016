package ru.spbau.mit.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.util.FileUtils;
import ru.spbau.mit.util.RepoUtils;

/**
 * Created by Эдгар on 30.09.2016.
 */
@Getter
@AllArgsConstructor
public class Environment {
    private Repository repository;
    private FileUtils fileUtils;
    private RepoUtils repoUtils;
}
