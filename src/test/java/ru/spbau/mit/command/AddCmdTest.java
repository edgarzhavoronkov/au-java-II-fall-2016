package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Repository repository;

    private Command add = new AddCmd();

    @Before
    public void setUp() throws Exception {
        when(core.getRepository()).thenReturn(repository);
        doNothing().when(repository).addFiles(any(String[].class));
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {"a", "b", "c"};
            final String result = add.execute(core, args);
            assertEquals("Added 3 file(s)", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

}