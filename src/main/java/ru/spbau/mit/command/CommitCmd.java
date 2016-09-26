package ru.spbau.mit.command;

import ru.spbau.mit.commit.Commit;
import ru.spbau.mit.repository.Repository;
import ru.spbau.mit.util.Hasher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class CommitCmd implements Command {
    @Override
    public String execute(Repository repository, String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date();
        String commitNumber = dateFormat.format(date);
        String commitMessage = "";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-m")) {
                if (i + 1 < args.length) {
                    commitMessage = args[i+1];
                    commitMessage = commitMessage.replaceAll("^\"", "").replaceAll("(\"$)", "");
                } else {
                    return "Commit message not provided!";
                }
            }
        }
        Commit commit = new Commit(commitNumber
                , commitMessage
                , repository.getAddedFiles()
        );

        String pathToCommit = String.format("%s/.repo/branches/%s/%s"
                , repository.getRepoPath()
                , repository.getCurrentBranch().getName()
                , commitNumber
        );
        Path commitPath = Paths.get(pathToCommit);
        Path descriptionPath = Paths.get(pathToCommit + "/description");
        try {
            Files.createDirectory(commitPath);
            Files.write(descriptionPath, commitMessage.getBytes(), StandardOpenOption.CREATE);
            for (String filename : repository.getAddedFiles()) {
                Path filePath = Paths.get(repository.getRepoPath() + filename);
                String targetFileName = String.format("%s/.repo/branches/%s/%s/%s"
                        , repository.getRepoPath()
                        , repository.getCurrentBranch().getName()
                        , commitNumber
                        , filename
                );
                Path resultPath = Paths.get(targetFileName);
                Files.copy(filePath, resultPath);
            }
            repository.getCurrentBranch().addCommit(commit);
            repository.setAddedFiles(new ArrayList<>());
        } catch (IOException e) {
            return "Some I/O problem occurred! Maybe file not present or else";
        }
        return String.format("Commit %s created", commitNumber);
    }
}
