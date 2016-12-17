package ru.spbau.mit.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import ru.spbau.mit.model.core.VcsCore;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * Created by Эдгар on 01.10.2016.
 * Small class for getting sha-1 hashes from files, taking relative paths, etc.
 */
public class FileSystem {
    public static String getRelativePath(File file, String workingDirectory) {
        Path base = Paths.get(workingDirectory).toAbsolutePath().normalize();
        Path filepath = Paths.get(file.getAbsolutePath()).normalize();
        Path relative = base.relativize(filepath).normalize();
        return relative.toString();
    }

    public static String getNewHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int n = in.read(buffer);
                while (n != -1) {
                    digest.update(buffer, 0, n);
                    n = in.read(buffer);
                }
            }
            return DatatypeConverter.printHexBinary(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lists all files in repository except ones, that are located in .repo dir
     * @param dir directory to list
     * @return Collection of filenames
     */
    public static Collection<File> listExternalFiles(File dir) {
        return FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, new AbstractFileFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !VcsCore.VCS_FOLDER_NAME.equals(name);
            }
        });
    }
}
