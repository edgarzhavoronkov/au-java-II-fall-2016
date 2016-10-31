package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CleanCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Repository repository;

    private Command clean = new CleanCmd();

    @Before
    public void setUp() throws Exception {
        when(core.getRepository()).thenReturn(repository);
        doNothing().when(repository).clean();
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {};
            final String result = clean.execute(core, args);
            assertEquals("Successfully cleaned", result);
        } catch (CommandFailException e) {
            fail();
        }
    }
}