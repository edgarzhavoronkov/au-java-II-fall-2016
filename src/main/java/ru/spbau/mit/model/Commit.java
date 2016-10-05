package ru.spbau.mit.model;

import lombok.Data;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for commit in VCS
 */
@Data
public class Commit  {
    private final long number;
    private final String message;
    private final long parentCommitNumber;
}
