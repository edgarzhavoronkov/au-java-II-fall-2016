package ru.spbau.mit.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.model.core.VcsCore;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * Created by Эдгар on 30.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckoutCmdTest {
    @Mock
    private VcsCore core;

    private Command checkout = new CheckoutCmd();

    @Before
    public void setUp() throws Exception {
        doNothing().when(core).checkoutBranch(anyString());
        doNothing().when(core).checkoutCommit(anyLong());
    }

    @Test
    public void testCheckoutCommit() throws Exception {
        try {
            final String[] args = {"-c", "123"};
            String result = checkout.execute(core, args);
            assertEquals("Checked out commit number 123", result);
        } catch (CommandFailException e) {
            fail();
        }
    }

    @Test
    public void testCheckoutBranch() throws Exception {
        try {
            final String[] args = {"-b", "branch"};
            final String result = checkout.execute(core, args);
            assertEquals("Checked out branch branch", result);
        } catch (CommandFailException e) {
            fail();
        }
    }
}