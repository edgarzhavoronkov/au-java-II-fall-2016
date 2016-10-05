package ru.spbau.mit.model;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import ru.spbau.mit.model.core.VcsCore;
import ru.spbau.mit.exceptions.MergeFailedException;
import ru.spbau.mit.io.SnapshotReader;
import ru.spbau.mit.io.SnapshotWriter;
import ru.spbau.mit.util.FileSystem;

import java.io.File;
import java.io.IOException;
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
     */
    public void addFiles(String[] filenames) {
        processFiles(filenames, this::addFile);
    }

    /**
     * Removes filenames from index
     * @param filenames Array of {@link String} with filenames to remove
     */
    public void removeFiles(String[] filenames) {
        processFiles(filenames, this::removeFile);
    }

    /**
     * Cleans the repository from untracked files
     */
    public void clean() {
        Snapshot snapshot = getCurrentSnapshot();
        snapshot.filenameSet()
                .stream()
                .filter(file -> !trackedFiles.contains(file))
                .forEach(file -> FileUtils.deleteQuietly(new File(workingDirectory, file)));
    }

    /**
     * Removes file from the index in particular {@link Commit}
     * @param filename file to remove from index
     * @param commitNumber commit number when to reset file
     * @throws IOException if I/O problem occured
     */
    public void resetFile(String filename, long commitNumber) throws IOException {
        Snapshot snapshot = getSnapshotByCommitNumber(commitNumber);
        if (!snapshot.contains(filename)) {
            trackedFiles.remove(filename);
        } else {
            String hash = snapshot.getFileHash(filename);
            FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDirectory, filename));
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
     * @throws IOException if deserialization of {@link Snapshot} failed
     */
    public Snapshot getSnapshotByCommitNumber(long commitNumber) throws IOException {
        File snapshotFile = getSnapshotFile(commitNumber);
        return SnapshotReader.readSnapshot(snapshotFile);
    }

    /**
     * Serializes commit to disk as a {@link Snapshot}
     * @param commitNumber commit to dump
     * @throws IOException if serialization failed
     */
    public void saveCommit(long commitNumber) throws IOException {
        Snapshot snapshot = new Snapshot();
        for (String filename : trackedFiles) {
            File file = new File(workingDirectory, filename);
            if (file.exists()) {
                String hash = FileSystem.getNewHash(file);
                snapshot.addFile(filename, hash);
                File dataFile = new File(getDataDirectory(), "data");
                if (!dataFile.exists()) {
                    FileUtils.copyFile(file, dataFile);
                }
            }
        }
        SnapshotWriter.writeSnapshot(snapshot, getSnapshotFile(commitNumber));
    }

    /**
     * Checkouts commit with given number. Does all the job with filesystem
     * Version in {@link VcsCore} wraps this one
     * @param commitNumber commit to checkout
     * @throws IOException if I/O problems occured
     */
    public void checkoutCommit(long commitNumber) throws IOException {
        for (String file : trackedFiles) {
            FileUtils.deleteQuietly(new File(workingDirectory, file));
        }
        Snapshot snapshot = SnapshotReader.readSnapshot(getSnapshotFile(commitNumber));
        trackedFiles.clear();
        trackedFiles.addAll(snapshot.filenameSet());
        for (String file : trackedFiles) {
            String hash = snapshot.getFileHash(file);
            FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDirectory, file));
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
     * @throws MergeFailedException if there is a conflict
     */
    public void merge(long srcCommitNumber
            , long dstCommitNumber
            , long baseCommitNumber
            , long nextCommitNumber) throws MergeFailedException {
        Snapshot srcSnapshot;
        Snapshot dstSnapshot;
        Snapshot baseSnapshot;
        try {
            srcSnapshot = getSnapshotByCommitNumber(srcCommitNumber);
            dstSnapshot = getSnapshotByCommitNumber(dstCommitNumber);
            baseSnapshot = getSnapshotByCommitNumber(baseCommitNumber);
        } catch (IOException e) {
            throw new MergeFailedException("Failed to read snapshot from disk!");
        }


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
                throw new MergeFailedException(String.format("Merge conflict in file%s. Aborting!", filename));
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
            SnapshotWriter.writeSnapshot(result, getSnapshotFile(nextCommitNumber));
        } catch (IOException e) {
            throw new MergeFailedException("Failed to write result of a merge to disk");
        }
    }

    private void processFiles(String[] filenames, Consumer<File> consumer) {
        for (String filename : filenames) {
            File file = new File(filename);
            if (file.isDirectory()) {
                FileSystem.listExternalFiles(file).forEach(consumer);
            } else {
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
