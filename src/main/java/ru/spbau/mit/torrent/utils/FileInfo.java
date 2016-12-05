package ru.spbau.mit.torrent.utils;

import lombok.Data;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эдгар on 30.10.2016.
 */
@Data
public class FileInfo {
    private final long fileId;
    private final String name;
    private final long size;

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong(fileId);
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeLong(size);
    }
}
