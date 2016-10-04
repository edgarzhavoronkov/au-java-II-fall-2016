package ru.spbau.mit.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for commit in VCS
 * Knows about {@link Branch}, parent commit
 * Also knows information about added and removed files
 */
@Data
public class Commit implements Serializable {
    private final long number;
    private final String message;
    private final long parentCommitNumber;
}
