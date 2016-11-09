package ru.spbau.mit;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.client.SimpleClient;
import ru.spbau.mit.exceptions.ServerException;
import ru.spbau.mit.server.SimpleServer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.*;

/**
 * Created by Эдгар on 15.10.2016.
 */
public class SimpleClientServerTest {
    private final SimpleServer server = new SimpleServer();
    private final SimpleClient client = new SimpleClient();

    private Thread serverThread = new Thread (() -> {
        try {
            server.start(8080);
        } catch (ServerException e) {
            fail(e.getMessage());
        }
    });

    @Before
    public void setUp() throws Exception {
        serverThread.start();
        client.connect("localhost", 8080);
    }

    @Test
    public void testNonEmptyDirectoryList() throws Exception {
        Map<String, Boolean> expected = new HashMap<>();
        expected.put("a", false);
        Map<String, Boolean> actual = client.executeList("src/test/resources/a");
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyDirectoryList() throws Exception {
        Map<String, Boolean> expected = Collections.EMPTY_MAP;
        Map<String, Boolean> actual = client.executeList("src/test/resources/b");
        assertEquals(expected, actual);
    }

    @Test
    public void testExistingFileGet() throws Exception {
        File onServer = new File("src/test/resources/a/a");
        client.executeGet("src/test/resources/a/a", "src/test/resources/a_get");
        File result = new File("src/test/resources/a_get");
        assertTrue(FileUtils.contentEquals(onServer, result));
        //noinspection ResultOfMethodCallIgnored
        result.delete();
    }

    @Test
    public void testNonExistingFileGet() throws Exception {
        client.executeGet("src/test/resources/a/b", "src/test/resources/b_get");
        assertFalse(new File("src/test/resources/b_get").exists());
    }

    @Test
    public void testManyConnections() throws Exception {
        List<Thread> clients = new ArrayList<>();

        Map<String, Boolean> expected = new HashMap<>();
        expected.put("a", true);
        expected.put("b", true);
        expected.put("res", false);

        CyclicBarrier barrier = new CyclicBarrier(10);

        for (int i = 0; i < 10; ++i) {
            clients.add(new Thread( () -> {
                SimpleClient simpleClient = new SimpleClient();
                try {
                    simpleClient.connect("localhost", 8080);

                    barrier.await();
                    Map<String, Boolean> actual = simpleClient.executeList("src/test/resources");

                    assertEquals(expected, actual);
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    fail();
                } finally {
                    try {
                        simpleClient.disconnect();
                    } catch (IOException e) {
                        fail(e.getMessage());
                    }
                }
            }));
        }

        clients.forEach(Thread::start);

        clients.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        client.disconnect();
        serverThread.join();
    }
}