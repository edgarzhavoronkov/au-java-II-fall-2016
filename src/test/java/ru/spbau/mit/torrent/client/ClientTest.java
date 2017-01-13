package ru.spbau.mit.torrent.client;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.torrent.common.ConnectionsHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Эдгар on 25.12.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientTest {
    @Rule
    public final TemporaryFolder root = new TemporaryFolder();

    @Mock
    private Client client;

    @Mock
    private ConnectionsHandler handler;

    @Before
    public void setUp() throws Exception {
        when(client.getConnectionsHandler()).thenReturn(handler);
        when(client.executeList()).thenCallRealMethod();
        doCallRealMethod().when(client).executeUpload(anyString());
        doCallRealMethod().when(client).executeGet(anyLong());

        doNothing().when(handler).sendRequest(any(InetSocketAddress.class), any(ClientTask.class));
        doNothing().when(handler).handleRequest(any(InetAddress.class), any(DataInputStream.class), any(DataOutputStream.class));
    }

    @Test
    public void testExecuteList() throws Exception {
        client.executeList();
        verify(handler, times(1)).sendRequest(any(), any());
    }

    @Test
    public void testExecuteUpload() throws Exception {
        File root = new File(".");
        File seed1File = new File(root, "a.txt");
        FileUtils.write(seed1File, "This is a sample file", Charset.forName("utf-8"));
        client.executeUpload("a.txt");
        verify(handler, times(2)).sendRequest(any(), any());
    }

    //since we don't have any files in that mock
    @Test(expected = FileNotFoundException.class)
    public void testExecuteGet() throws Exception {
        client.executeGet((long) 123);
    }
}