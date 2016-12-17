package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Commit;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Commit commit;

    private Command log = new LogCmd();

    @Before
    public void setUp() throws Exception {
        when(core.getCurrentCommit()).thenReturn(commit);
        when(commit.getMessage()).thenReturn("message");
        when(commit.getNumber()).thenReturn(Long.valueOf(123));
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {};
            final String result = log.execute(core, args);
            assertEquals("123 : message\n", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

}