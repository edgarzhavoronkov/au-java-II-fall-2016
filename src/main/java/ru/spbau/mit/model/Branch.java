package ru.spbau.mit.model;

import lombok.*;

import java.io.Serializable;

/**
 * Created by Эдгар on 25.09.2016.
 * Abstraction for Branch in VCS
 * Knows it's name and all commits in done in that branch
 * Identified by name
 */
@Data
@AllArgsConstructor
public class Branch implements Serializable {
    private final String name;
    private long headCommitNumber;
}
