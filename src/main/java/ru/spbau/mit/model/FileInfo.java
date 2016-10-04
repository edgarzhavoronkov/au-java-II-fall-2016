package ru.spbau.mit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Эдгар on 01.10.2016.
 * Class that encapsulates information about file
 * Namely, knows relative path to file and when the file was last updated
 * second field is used in {@link Repository} to track
 * whether we should add file to commit or not
 */
@Data
public class FileInfo implements Serializable {

}
