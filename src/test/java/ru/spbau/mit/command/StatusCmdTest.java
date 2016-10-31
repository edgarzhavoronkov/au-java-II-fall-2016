package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.Snapshot;
import ru.spbau.mit.model.core.VcsCore;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Repository repository;

    @Mock
    private Commit commit;

    @Mock
    private Snapshot curr;

    @Mock
    private Snapshot prev;

    private Command status = new StatusCmd();

    @Before
    public void setUp() throws Exception {
        when(core.getRepository()).thenReturn(repository);
        when(core.getCurrentCommit()).thenReturn(commit);
        when(commit.getParentCommitNumber()).thenReturn(Long.valueOf(123));
        when(repository.getCurrentSnapshot()).thenReturn(curr);
        when(repository.getSnapshotByCommitNumber(anyLong())).thenReturn(prev);

        Set<String> trackedFiles = new HashSet<>();
        trackedFiles.add("a");
        trackedFiles.add("b");
        trackedFiles.add("c");

        when(repository.getTrackedFiles()).thenReturn(trackedFiles);

        Set<String> currFiles = new HashSet<>();
        currFiles.add("b");
        currFiles.add("c");

        when(curr.filenameSet()).thenReturn(currFiles);
        when(curr.contains("b")).thenReturn(true);
        when(curr.contains("c")).thenReturn(true);

        Set<String> prevFiles = new HashSet<>();

        when(prev.filenameSet()).thenReturn(prevFiles);
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {};
            final String result = status.execute(core, args);
            assertEquals("Added files: \n" +
                    "\tb\n" +
                    "\tc\n", result);
        } catch (CommandFailException e) {
            fail();
        }

    }

}