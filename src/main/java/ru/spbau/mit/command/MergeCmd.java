package ru.spbau.mit.command;

import ru.spbau.mit.environment.Environment;
import ru.spbau.mit.model.FileInfo;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.exceptions.CommandFailException;

import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class MergeCmd implements Command {
    @Override
    public String execute(Environment environment, String[] args) {
        if (environment.getRepoUtils().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length != 2) {
            throw new CommandFailException("Merge needs two branches' names in arguments");
        }

        String srcBranchName = args[0];
        String dstBranchName = args[1];

        String lastCommitNumberInSrc = environment
                .getRepository()
                .getBranchByName(srcBranchName)
                .getCommits()
                .get(environment
                        .getRepository()
                        .getBranchByName(srcBranchName)
                        .getCommits()
                        .size() - 1)
                .getCommitNumber();

        String lastCommitNumberInDst = environment
                .getRepository()
                .getBranchByName(dstBranchName)
                .getCommits()
                .get(environment
                        .getRepository()
                        .getBranchByName(dstBranchName)
                        .getCommits()
                        .size() - 1)
                .getCommitNumber();

        List<Commit> pathFromSrc = environment
                .getRepoUtils()
                .getPathFromCommit(environment.getRepository(), lastCommitNumberInSrc);

        List<Commit> pathFromDst = environment
                .getRepoUtils()
                .getPathFromCommit(environment.getRepository(), lastCommitNumberInDst);

        int i = pathFromSrc.size() - 1;
        int j = pathFromDst.size() - 1;

        while (pathFromSrc.get(i).equals(pathFromDst.get(j))) {
            if (i > 0) i--;
            if (j > 0) j--;
        }

        Commit ancestor;
        if (i == 0 || j == 0) {
            ancestor = i == 0 ? pathFromSrc.get(i) : pathFromDst.get(j);
        } else {
            ancestor = pathFromSrc.get(i + 1);
        }

        environment.getFileUtils().clearProject();

        Map<FileInfo, Commit> changes = environment.getRepoUtils()
                .collectChanges(environment.getRepository(), ancestor.getCommitNumber());

        environment.getRepoUtils().copyFromCommitDirs(changes);

        return String.format("Merged branch %s into %s", srcBranchName, dstBranchName);

    }
}
