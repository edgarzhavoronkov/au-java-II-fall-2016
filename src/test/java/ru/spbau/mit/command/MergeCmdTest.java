package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.Branch;
import ru.spbau.mit.model.Repository;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MergeCmdTest {
    @Mock
    private VcsCore core;

    @Mock
    private Branch branch;

    private Command merge = new MergeCmd();

    @Before
    public void setUp() throws Exception {
        doNothing().when(core).merge(anyString());
        when(core.getCurrentBranch()).thenReturn(branch);
        when(branch.getName()).thenReturn("master");
    }

    @Test
    public void testExecute() throws Exception {
        try {
            final String[] args = {"branch"};
            final String result = merge.execute(core, args);
            assertEquals("Merged branch branch into master", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

}