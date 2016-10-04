package ru.spbau.mit.model;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import ru.spbau.mit.core.VcsCore;
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
 */
public class Repository {
    private final String workingDirectory;
    @Getter
    private final Set<String> trackedFiles = new HashSet<>();

    public Repository(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void addFiles(String[] filenames) {
        processFiles(filenames, this::addFile);
    }

    public void removeFiles(String[] filenames) {
        processFiles(filenames, this::removeFile);
    }

    public void clean() {
        Snapshot snapshot = getCurrentSnapshot();
        snapshot.filenameSet()
                .stream()
                .filter(file -> !trackedFiles.contains(file))
                .forEach(file -> FileUtils.deleteQuietly(new File(workingDirectory, file)));
    }

    public void resetFile(String filename, long commitNumber) throws IOException {
        Snapshot snapshot = getSnapshotByCommitNumber(commitNumber);
        if (!snapshot.contains(filename)) {
            trackedFiles.remove(filename);
        } else {
            String hash = snapshot.getFileHash(filename);
            FileUtils.copyFile(new File(getDataDirectory(), hash), new File(workingDirectory, filename));
        }
    }

    public Snapshot getCurrentSnapshot() {
        Collection<File> allFiles = FileSystem.listExternalFiles(new File(workingDirectory));
        Snapshot snapshot = new Snapshot();
        for (File file : allFiles) {
            String relativePath = FileSystem.getRelativePath(file, workingDirectory);
            snapshot.addFile(relativePath, FileSystem.getNewHash(file));
        }
        return snapshot;
    }

    public Snapshot getSnapshotByCommitNumber(long commitNumber) {
        File snapshotFile = getSnapshotFile(commitNumber);
        return SnapshotReader.readSnapshot(snapshotFile);
    }

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

    public void merge(long srcCommitNumber
            , long dstCommitNumber
            , long baseCommitNumber
            , long nextCommitNumber) throws MergeFailedException {
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
        SnapshotWriter.writeSnapshot(result, getSnapshotFile(nextCommitNumber));
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
