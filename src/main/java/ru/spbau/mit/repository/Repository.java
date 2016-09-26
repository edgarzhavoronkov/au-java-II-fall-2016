package ru.spbau.mit.repository;

import ru.spbau.mit.branch.Branch;
import ru.spbau.mit.command.Command;
import ru.spbau.mit.command.CommandProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by Эдгар on 25.09.2016.
 */
public class Repository {
    private final Map<String, Branch> branches = new HashMap<>();
    private final String repoPath;

    private List<String> addedFiles = new ArrayList<>();
    private Branch currentBranch;

    //TODO: add logger

    public Repository() throws IOException {
        repoPath = ".";
        new Repository(".");
    }

    public Repository(String path) throws IOException {
        repoPath = path;
        Path repo = Paths.get(repoPath + "/.repo/branches/def");
        if (!Files.exists(repo)) {
            Files.createDirectories(repo);
        }
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
                Paths.get(
                        String.format(
                                "%s/.repo/branches/"
                                , repoPath
                        )
                )
        );
        for (Path p : directoryStream) {
            String branchName = p.getFileName().toString();
            branches.put(branchName, new Branch(branchName));
            //TODO: set all commits
        }
        currentBranch = branches.get("def");
    }

    public String execute(String input) {
        //TODO: parse instead of splitting
        String[] split = input.split("\\s+");
        String cmdName = split[0];
        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
        Command cmd = CommandProvider.forName(cmdName);
        if (cmd != null) {
            return cmd.execute(this, args);
        }
        return "Unknown command, please be more precise!";
    }

    public String getRepoPath() {
        return repoPath;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public List<String> getAddedFiles() {
        return addedFiles;
    }

    public void setAddedFiles(List<String> addedFiles) {
        this.addedFiles = addedFiles;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public void setCurrentBranch(Branch currentBranch) {
        this.currentBranch = currentBranch;
    }
}
