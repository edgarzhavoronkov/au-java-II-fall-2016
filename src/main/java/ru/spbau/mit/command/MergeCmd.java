package ru.spbau.mit.command;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.commit.Commit;
import ru.spbau.mit.repository.Repository;
import ru.spbau.mit.util.Hasher;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
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
                    Commit lastCommitInSrc = src.getCommits().get(src.getCommits().size() - 1);
                    Commit lastCommitInDst = dst.getCommits().get(dst.getCommits().size() - 1);

                    //unholy bottleneck
                    for (String filename1 : lastCommitInSrc.getFiles()) {
                        for (String filename2 : lastCommitInDst.getFiles()) {
                            String hash1 = Hasher.getFileHash(filename1);
                            String hash2 = Hasher.getFileHash(filename2);
                            if (hash1 != null) {
                                if (filename1.equals(filename2) && !hash1.equals(hash2)) {
                                    return "Conflict in merge. Aborting!";
                                }
                            }
                        }
                    }

                    List<String> newFiles = new ArrayList<>();
                    newFiles.addAll(lastCommitInSrc.getFiles());
                    newFiles.addAll(lastCommitInDst.getFiles());

                    String newCommitNumber = Hasher.getHash(String.valueOf(System.currentTimeMillis()));
                    String newCommitMessage = String.format("Merge branch %s into branch %s", args[0], args[1]);
                    Commit newCommit = new Commit(newCommitNumber, newCommitMessage, newFiles);

                    Path destination = Paths.get(
                            String.format(
                                    "%s/.repo/branches/%s"
                                    , repository.getRepoPath()
                                    , newCommitNumber
                            )
                    );

                    dst.getCommits().add(newCommit);

                    for (String filename : newFiles) {
                        try {
                            Files.copy(Paths.get(filename), destination);
                        } catch (IOException e) {
                            return "Merge failed due to I/O failure.";
                        }
                    }
                    return String.format("Merged branch %s into %s at commit %s!", args[0], args[1], newCommitNumber);
                } else {
                    return "Can't merge two empty branches since it doesn't make sense";
                }
            } else {
                return "I can merge only existing branches";
            }
        }
    }
}
