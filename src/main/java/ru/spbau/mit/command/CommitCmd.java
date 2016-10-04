package ru.spbau.mit.command;

import ru.spbau.mit.core.VcsCore;
import ru.spbau.mit.model.FileInfo;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.exceptions.CommandFailException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class CommitCmd implements Command {
    @Override
    public String execute(VcsCore vcs, String[] args) {
        if (!vcs.getVcsCore().isInit()) {
            throw new CommandFailException("Repository has not been init");
        }

        if (args.length != 2) {
            throw new CommandFailException("Wrong arguments format! Use commit -m and commit message");
        }

        if (args[0].equals("-m")) {
            String commitMessage = args[1];

            Map<FileInfo, Commit> changes = vcs.getVcsCore().collectChanges(vcs.getRepository());

            List<FileInfo> modifiedFiles = new ArrayList<>();
            List<FileInfo> removedFiles = new ArrayList<>();

            Path currentDirectory = vcs.getFileUtils().getCurrentDirectory();

            for (FileInfo fileInfo : changes.keySet()) {
                File file = new File(currentDirectory.toFile(), fileInfo.getPath());
                if (file.exists()) {
                    if (file.lastModified() > fileInfo.getLastUpdated()) {
                        modifiedFiles.add(new FileInfo(file.getPath(), file.lastModified()));
                    }
                } else {
                    removedFiles.add(fileInfo);
                }
            }

            List<String> addedFiles = vcs.getVcsCore().getAddedFiles();
            for (String filename : addedFiles) {
                File file = new File(currentDirectory.toFile(), filename);
                if (file.exists()) {
                    String path = currentDirectory.relativize(Paths.get(file.getAbsolutePath())).toString();
                    modifiedFiles.add(new FileInfo(path, file.lastModified()));
                }
            }

            Commit commit = vcs.getRepository().addNewCommit(commitMessage, modifiedFiles, removedFiles);

            vcs.getVcsCore().copyFilesToCommitDirectory(commit);
            vcs.getVcsCore().clearStagedFiles();

            return String.format("Commit %s created", commit.getCommitNumber());
        } else {
            throw new CommandFailException("Wrong key! Use -m and provide commit message");
        }
    }
}
