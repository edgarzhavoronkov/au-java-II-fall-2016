package ru.spbau.mit.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.mit.util.RequestType;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Эдгар on 08.10.2016.
 * Class for simple client
 * Allows two types of queries
 * 1. List
 * <1 : Int> <path : String>
 * where path - path to a directory on server to show its' list of files
 * 2. Get
 * <2 : Int> <path : String>
 * where path - path to a file on server that we want to download
 */
public class SimpleClient {
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final static Logger log = LogManager.getLogger(SimpleClient.class);

    /**
     * Connects to host and knocks to port
     * @param hostname name of host to connect
     * @param port port to knock into
     * @throws IOException if connection fails
     */
    public void connect(String hostname, int port) throws IOException {
        clientSocket = new Socket(hostname, port);
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        log.info(String.format("Connected to host %s to port %d", hostname, port));
    }

    /**
     * Closes current connection
     * @throws IOException if fails
     */
    public void disconnect() throws IOException {
        inputStream.close();
        outputStream.close();
        clientSocket.close();
        log.info(String.format("Disconnected from %s", clientSocket.getInetAddress()));
    }

    /**
     * Executes LIST query. To be more precise, sends requests,
     * waits for reply and processes reply from server
     * @param path path to directory on server to show list of files' of
     * @return Map where keys are filenames and values are indicating whether file is directory or not
     * @throws IOException if something fails
     */
    public Map<String, Boolean> executeList(String path) throws IOException {
        sendRequest(RequestType.LIST, path);
        return processList();
    }

    /**
     * Executes GET query. Sends request to server
     * then waits for reply and creates a corresponding file
     * @param path path to file on server to get
     * @throws IOException if something fails
     */
    public void executeGet(String path, String dst) throws IOException {
        sendRequest(RequestType.GET, path);
        processGet(dst);
    }

    private void sendRequest(RequestType request, String path) throws IOException {
        outputStream.writeInt(request.ordinal());
        outputStream.writeUTF(path);
        outputStream.flush();
        log.info(String.format("Sent %s request to server. Waiting for reply", request.toString()));
    }

    private Map<String,Boolean> processList() throws IOException {
        log.info("Processing list request");
        int size = inputStream.readInt();
        log.info("Receiving reply!");
        log.info(String.format("Received size: %d", size));
        if (size == -1) {
            return null;
        } else {
            Map<String, Boolean> res = new HashMap<>();
            for (int i = 0; i < size; ++i) {
                String name = inputStream.readUTF();
                Boolean isDir = inputStream.readBoolean();
                res.put(name, isDir);
            }
            log.info("Received list of files");
            return res;
        }
    }

    private void processGet(String path) throws IOException {
        log.info("Processing get request");
        long size = inputStream.readLong();
        log.info("Receiving reply!");
        log.info(String.format("Received size: %d", size));
        if (size != 0) {
            File result = new File(path);
            try (FileOutputStream fileOutputStream = new FileOutputStream(result)) {
                byte[] buffer = new byte[1024];

                for (int i = 0, len; i < size; i += len) {
                    len = (int) (size - i > buffer.length ? buffer.length : size - i);
                    //noinspection ResultOfMethodCallIgnored
                    inputStream.read(buffer, 0, len);
                    fileOutputStream.write(buffer, 0, len);
                }
                log.info("Received file's bytes");
                //noinspection ResultOfMethodCallIgnored
                result.createNewFile();
                fileOutputStream.flush();
            }
        }
    }
}
