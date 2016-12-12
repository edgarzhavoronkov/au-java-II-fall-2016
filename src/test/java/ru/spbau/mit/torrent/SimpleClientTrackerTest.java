package ru.spbau.mit.torrent;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.spbau.mit.torrent.client.Client;
import ru.spbau.mit.torrent.exceptions.UploadFailException;
import ru.spbau.mit.torrent.tracker.Tracker;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by Эдгар on 12.12.2016.
 */
public class SimpleClientTrackerTest {
    @Rule
    public final TemporaryFolder root = new TemporaryFolder();

    private File trackerRoot;
    private File seed1Root;
    private File seed2Root;

    private Tracker tracker;
    private Client seed1;
    private Client seed2;


    @Before
    public void setUp() throws Throwable {
        trackerRoot = root.newFolder();
        seed1Root = root.newFolder();
        seed2Root = root.newFolder();

        tracker = new Tracker(trackerRoot.getAbsolutePath());
        seed1 = new Client(
                new InetSocketAddress(
                        InetAddress.getByName("localhost")
                        , Tracker.TRACKER_PORT
                )
                , seed1Root.getAbsolutePath()
        );
        seed2 = new Client(
                new InetSocketAddress(
                        InetAddress.getByName("localhost")
                        , Tracker.TRACKER_PORT
                )
                , seed2Root.getAbsolutePath()
        );

        tracker.start();
        seed1.start(12345);
        seed2.start(54321);
    }

    @Test
    public void testSimple() throws Throwable {
        File seed1File = new File(seed1Root, "a.txt");
        FileUtils.write(seed1File, "This is a sample file", Charset.forName("utf-8"));

        seed1.executeUpload(seed1File.getName());

        List<FileInfo> files1 = seed1.executeList();
        List<FileInfo> files2 = seed2.executeList();
        assertEquals(1, files1.size());
        assertEquals(1, files2.size());

        long fileId = files1.get(0).getFileId();

        seed2.executeGet(fileId);

        Thread.sleep(1000);

        File seed2File = new File(seed2Root, "a.txt");
        assertTrue(seed2File.exists());
    }

    @After
    public void tearDown() throws Throwable {
        tracker.stop();
        seed1.stop();
        seed2.stop();
    }
}
