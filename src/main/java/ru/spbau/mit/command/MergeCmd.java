package ru.spbau.mit.command;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.commit.Commit;
import ru.spbau.mit.repository.Repository;
import ru.spbau.mit.util.Hasher;

import java.io.IOException;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class MergeCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        if (args.length != 2) {
            return "I can't merge more than two branches! Sorry =(";
        } else {
            Branch src = repository.getBranches().get(args[0]);
            Branch dst = repository.getBranches().get(args[1]);
            if (src != null && dst != null) {
                if (!src.getCommits().isEmpty() && !dst.getCommits().isEmpty()) {
                    Commit lastCommitInSrc = src.getCommits().get(0);
                    Commit lastCommitInDst = dst.getCommits().get(0);

                    //unholy bottleneck
                    for (String filename1 : lastCommitInSrc.getFiles()) {
                        for (String filename2 : lastCommitInDst.getFiles()) {
                            String fullPath1 = String.format(
                                    "%s/.repo/branches/%s/%s/%s"
                                    , repository.getRepoPath()
                                    , args[0]
                                    , lastCommitInSrc.getNumber()
                                    , filename1
                            );
                            String fullPath2 = String.format(
                                    "%s/.repo/branches/%s/%s/%s"
                                    , repository.getRepoPath()
                                    , args[1]
                                    , lastCommitInDst.getNumber()
                                    , filename2
                            );
                            String hash1 = Hasher.getFileHash(fullPath1);
                            String hash2 = Hasher.getFileHash(fullPath2);
                            if (hash1 != null && hash2 != null) {
                                if (filename1.equals(filename2) && !hash1.equals(hash2)) {
                                    return "Conflict in merge. Aborting!";
                                }
                            }
                        }
                    }

                    List<String> filesToCopy = new ArrayList<>();
                    filesToCopy.addAll(lastCommitInSrc.getFiles());
                    filesToCopy.addAll(lastCommitInDst.getFiles());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                    Date date = new Date();
                    String newCommitNumber = dateFormat.format(date);
                    String newCommitMessage = String.format("Merge branch %s into branch %s", args[0], args[1]);
                    Commit newCommit = new Commit(newCommitNumber, newCommitMessage, filesToCopy);
                    dst.getCommits().add(newCommit);

                    Path pathToNewCommit = Paths.get(
                            String.format(
                                "%s/.repo/branches/%s/%s/"
                                , repository.getRepoPath()
                                , args[1]
                                , newCommitNumber
                        )
                    );

                    try {
                        Files.createDirectory(pathToNewCommit);
                        Path descriptionPath = Paths.get(pathToNewCommit + "/description");
                        Files.write(descriptionPath, newCommitMessage.getBytes(), StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        return "Failed to create directory for new commit Aborting!";
                    }

                    for (String filename1 : lastCommitInSrc.getFiles()) {
                        String fullSourcePath = String.format(
                                "%s/.repo/branches/%s/%s/%s"
                                , repository.getRepoPath()
                                , args[0]
                                , lastCommitInSrc.getNumber()
                                , filename1
                        );
                        String fullTargetPath = String.format(
                                "%s/.repo/branches/%s/%s/%s"
                                , repository.getRepoPath()
                                , args[1]
                                , newCommitNumber
                                ,filename1
                        );
                        Path source = Paths.get(fullSourcePath);
                        Path target = Paths.get(fullTargetPath);
                        try {
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            return "Merge failed due to I/O failure";
                        }
                    }

                    for (String filename2 : lastCommitInDst.getFiles()) {
                        String fullSourcePath = String.format(
                                "%s/.repo/branches/%s/%s/%s"
                                , repository.getRepoPath()
                                , args[1]
                                , lastCommitInDst.getNumber()
                                , filename2
                        );
                        String fullTargetPath = String.format(
                                "%s/.repo/branches/%s/%s/%s"
                                , repository.getRepoPath()
                                , args[1]
                                , newCommitNumber
                                , filename2
                        );
                        Path source = Paths.get(fullSourcePath);
                        Path target = Paths.get(fullTargetPath);
                        try {
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            return "Merge failed due to I/O failure";
                        }
                    }
                    return String.format("Merged branch %s into %s at commit %s!", args[0], args[1], newCommitNumber);
                } else {
                    return "Can't merge if any branch is empty since it doesn't make sense";
                }
            } else {
                return "I can merge only existing branches";
            }
        }
    }
}
