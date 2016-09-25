package ru.spbau.mit.repository;

import ru.spbau.mit.branch.Branch;

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

    public Repository(String path) throws IOException {
        repoPath = path;
        Path repo = Paths.get(repoPath + "/branches/def");
        if (!Files.exists(repo)) {
            Files.createDirectories(repo);
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
                    Paths.get(
                            String.format(
                                    "%s/branches"
                                    , repoPath
                            )
                    )
            );
            for (Path p : directoryStream) {
                String branchName = p.getFileName().toString();
                branches.put(branchName, new Branch(branchName));
                System.out.println(p.getFileName());
            }
        }
        currentBranch = branches.get("def");
    }

    public String execute(String input) {
        return input;
    }

    private List<String> parseCommand(String rawInput) {
        return null;
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
