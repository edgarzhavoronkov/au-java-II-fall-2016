package ru.spbau.mit.model;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import ru.spbau.mit.exceptions.RepositoryException;
import ru.spbau.mit.model.core.VcsCore;
import ru.spbau.mit.exceptions.MergeFailedException;
import ru.spbau.mit.io.SnapshotSerializer;
import ru.spbau.mit.util.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for repository in VCS
 * knows about current working directory and tracked files
 */
public class Repository {
    private final String workingDirectory;
    @Getter
    private final Set<String> trackedFiles = new HashSet<>();

    /**
     * Constructor from working directory
     * @param workingDirectory
     */
    public Repository(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Adds filenames to index
     * @param filenames Array of {@link String} with filenames to add
     * @throws FileNotFoundException if any of the files was not found
     */
    public void addFiles(String[] filenames) throws FileNotFoundException {
        processFiles(filenames, this::addFile);
    }

    /**
     * removes files from index
     * @param filenames files to remove
     * @throws FileNotFoundException if any of files was not found
     */
    public void removeFiles(String[] filenames) throws FileNotFoundException {
        processFiles(filenames, this::removeFile);
    }

    /**
     * Cleans repository from untracked files
     * @throws RepositoryException if deletion failed
     */
    public void clean() throws RepositoryException {
        Snapshot snapshot = getCurrentSnapshot();
        for (String file : snapshot.filenameSet()) {
            if (!trackedFiles.contains(file)) {
                try {
                    FileUtils.forceDelete(new File(workingDirectory, file));
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
            }
        }
    }

    /**
     * Removes file from the index in particular {@link Commit}
     * @param filename file to remove from index
     * @param commitNumber commit number when to reset file
     * @throws RepositoryException if I/O problem occured
     */
    public void resetFile(String filename, long commitNumber) throws RepositoryException {
        try {
            Snapshot snapshot = getSnapshotByCommitNumber(commitNumber);
            if (!snapshot.contains(filename)) {
                trackedFiles.remove(filename);
            } else {
                String hash = snapshot.getFileHash(filename);
                FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDirectory, filename));
            }
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Get current state of repository. See {@link Snapshot}
     * @return current {@link Snapshot} of repo
     */
    public Snapshot getCurrentSnapshot() {
        Collection<File> allFiles = FileSystem.listExternalFiles(new File(workingDirectory));
        Snapshot snapshot = new Snapshot();
        for (File file : allFiles) {
            String relativePath = FileSystem.getRelativePath(file, workingDirectory);
            snapshot.addFile(relativePath, FileSystem.getNewHash(file));
        }
        return snapshot;
    }

    /**
     * Get state of repository by the commit number
     * @param commitNumber moment to get the {@link Snapshot}
     * @return specified {@link Snapshot} of repo
     * @throws RepositoryException if deserialization of {@link Snapshot} failed
     */
    public Snapshot getSnapshotByCommitNumber(long commitNumber) throws RepositoryException {
        File snapshotFile = getSnapshotFile(commitNumber);
        try {
            return SnapshotSerializer.readSnapshot(snapshotFile);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Serializes commit to disk as a {@link Snapshot}
     * @param commitNumber commit to dump
     * @throws RepositoryException if serialization failed
     */
    public void saveCommit(long commitNumber) throws RepositoryException {
        try{
            Snapshot snapshot = new Snapshot();
            for (String filename : trackedFiles) {
                File file = new File(workingDirectory, filename);
                if (file.exists()) {
                    String hash = FileSystem.getNewHash(file);
                    snapshot.addFile(filename, hash);
                    File dataFile = new File(getDataDirectory(), hash);
                    if (!dataFile.exists()) {
                        FileUtils.copyFile(file, dataFile);
                    }
                }
            }
            SnapshotSerializer.writeSnapshot(snapshot, getSnapshotFile(commitNumber));
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Checkouts commit with given number. Does all the job with filesystem
     * Version in {@link VcsCore} wraps this one
     * @param commitNumber commit to checkout
     * @throws RepositoryException if I/O problems happened
     */
    public void checkoutCommit(long commitNumber) throws RepositoryException {
        try {
            for (String file : trackedFiles) {
                FileUtils.deleteQuietly(new File(workingDirectory, file));
            }
            Snapshot snapshot = SnapshotSerializer.readSnapshot(getSnapshotFile(commitNumber));
            trackedFiles.clear();
            trackedFiles.addAll(snapshot.filenameSet());
            for (String file : trackedFiles) {
                String hash = snapshot.getFileHash(file);
                FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDirectory, file));
            }
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Actual three-way merging algorithm
     * Gets all the changes in src , dst and
     * base commits and checks if there is conflicts,
     * by comparing SHA-1 hashes of corresponding files
     * @param srcCommitNumber first commit
     * @param dstCommitNumber second commit
     * @param baseCommitNumber their common ancestor
     * @param nextCommitNumber merge-commit number
     * @throws RepositoryException if there is a conflict or writing new snapshot failed
     */
    public void merge(long srcCommitNumber
            , long dstCommitNumber
            , long baseCommitNumber
            , long nextCommitNumber) throws RepositoryException {
        Snapshot srcSnapshot = getSnapshotByCommitNumber(srcCommitNumber);
        Snapshot dstSnapshot = getSnapshotByCommitNumber(dstCommitNumber);
        Snapshot baseSnapshot = getSnapshotByCommitNumber(baseCommitNumber);

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(srcSnapshot.filenameSet());
        allFiles.addAll(dstSnapshot.filenameSet());
        allFiles.addAll(baseSnapshot.filenameSet());

        Snapshot result = new Snapshot();
        for (String filename : allFiles) {
            boolean isChangedInSrc = !Objects.equals(
                    srcSnapshot.getFileHash(filename)
                    , baseSnapshot.getFileHash(filename)
            );
            boolean isChangedInDst = !Objects.equals(
                    dstSnapshot.getFileHash(filename)
                    , baseSnapshot.getFileHash(filename)
            );
            boolean changesAreDifferent = !Objects.equals(
                    srcSnapshot.getFileHash(filename)
                    , dstSnapshot.getFileHash(filename)
            );

            boolean isConflict = isChangedInSrc && isChangedInDst && changesAreDifferent;

            if (isConflict) {
                throw new RepositoryException(String.format("Merge conflict in file%s. Aborting!", filename));
            }

            if (isChangedInSrc) {
                if (srcSnapshot.contains(filename)) {
                    result.addFile(filename, srcSnapshot.getFileHash(filename));
                }
                continue;
            }

            if (dstSnapshot.contains(filename)) {
                result.addFile(filename, dstSnapshot.getFileHash(filename));
            }
        }

        try {
            SnapshotSerializer.writeSnapshot(result, getSnapshotFile(nextCommitNumber));
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    private void processFiles(String[] filenames, Consumer<File> consumer) throws FileNotFoundException {
        for (String filename : filenames) {
            File file = new File(filename);
            if (file.isDirectory()) {
                FileSystem.listExternalFiles(file).forEach(consumer);
            } else {
                if (!file.exists()) {
                    throw new FileNotFoundException(file.getName());
                }
                consumer.accept(file);
            }
        }
    }

    private void addFile(File file) {
        String relativePath = FileSystem.getRelativePath(file, workingDirectory);
        trackedFiles.add(relativePath);
    }

    private void removeFile(File file) {
        String relativePath = FileSystem.getRelativePath(file, workingDirectory);
        trackedFiles.remove(relativePath);
        FileUtils.deleteQuietly(file);
    }

    /**
     * Literally, checks if given file is not outside the working directory
     * @param filepath - path to file to check
     * @return true - if yes, otherwise - false
     */
    public boolean isFileInRepo(String filepath) {
        String fullPathToWorkingDirectory = Paths.get(new File(workingDirectory).getAbsolutePath()).normalize().toString();
        String fullPathToFile = Paths.get(new File(filepath).getAbsolutePath()).normalize().toString();
        return fullPathToFile.startsWith(fullPathToWorkingDirectory);
    }

    private File getVcsFolder() {
        return new File(workingDirectory, VcsCore.VCS_FOLDER_NAME);
    }

    private File getSnapshotFile(long commitNumber) {
        return new File(getVcsFolder(), String.valueOf(commitNumber) + ".json");
    }

    private File getDataDirectory() {
        return new File(getVcsFolder(), "data");
    }
}
