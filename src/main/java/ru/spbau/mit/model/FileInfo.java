package ru.spbau.mit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Эдгар on 01.10.2016.
 */
@Data
public class FileInfo implements Serializable {
    private final String path;
    private final long lastUpdated;
}
