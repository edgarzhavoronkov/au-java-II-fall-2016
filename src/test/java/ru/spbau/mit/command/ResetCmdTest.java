package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResetCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Repository repository;

    @Mock
    private Commit commit;

    private Command reset = new ResetCmd();

    @Before
    public void setUp() throws Exception {
        when(core.getRepository()).thenReturn(repository);
        when(core.getCurrentCommit()).thenReturn(commit);
        when(commit.getNumber()).thenReturn(Long.valueOf(123));
        try {
            doNothing().when(repository).resetFile(anyString(), anyLong());
        } catch (ru.spbau.mit.exceptions.RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {"file"};
            final String result = reset.execute(core, args);
            assertEquals("Removed file file from index", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

}