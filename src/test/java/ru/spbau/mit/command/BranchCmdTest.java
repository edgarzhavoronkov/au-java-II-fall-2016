package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class BranchCmdTest {
    @Mock
    private VcsCore core;

    private Command branch;

    @Before
    public void setUp() throws Exception {
        branch = new BranchCmd();
        doNothing().when(core).createBranch(anyString());
        doNothing().when(core).removeBranch(anyString());
    }

    @Test
    public void testCreateBranch() throws Exception {
        try {
            final String[] args = { "-c",  "branch" };
            final String result = branch.execute(core, args);
            assertEquals("Created branch branch", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

    @Test
    public void testRemoveBranch() throws Exception {
        try {
            final String[] args = { "-r",  "branch" };
            final String result = branch.execute(core, args);
            assertEquals("Branch branch was removed!", result);
        } catch (CommandFailException e) {
            fail();
        }
    }
}