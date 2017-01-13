package ru.spbau.mit.torrent.utils;

import lombok.Data;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эдгар on 30.10.2016.
 * Information about file. Id, name and size
 */
@Data
public class FileInfo {
    private final long fileId;
    private final String name;
    private final long size;
}
