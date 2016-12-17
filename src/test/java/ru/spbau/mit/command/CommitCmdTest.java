package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommitCmdTest {
    @Mock
    private VcsCore core;

    private Command commit = new CommitCmd();

    @Before
    public void setUp() throws Exception {
        doNothing().when(core).commit(anyString());
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {"-m", "message"};
            final String result = commit.execute(core, args);
            assertEquals("Commit created!", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

}