package ru.spbau.mit.client;

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
    }

    /**
     * Closes current connection
     * @throws IOException if failes
     */
    public void disconnect() throws IOException {
        clientSocket.close();
    }

    /**
     * Executes LIST query. To be more precise, sends requests,
     * waits for reply and processes reply from server
     * @param path path to directory on server to show list of files' of
     * @return Map where keys are filenames and values are indicating whether file is directory or not
     * @throws IOException if something fails
     */
    public Map<String, Boolean> executeList(String path) throws IOException {
        sendRequest(1, path);
        return processList();

    }

    /**
     * Executes GET query. Sens request to server
     * then waits for reply and processes it
     * @param path path to file on server to get
     * @return java.io.File of corresponding content
     * @throws IOException if something fails
     */
    public File executeGet(String path, String dst) throws IOException {
        sendRequest(2, path);
        return processGet(dst);
    }

    private void sendRequest(int request, String path) throws IOException {
        outputStream.writeInt(request);
        outputStream.writeUTF(path);
        outputStream.flush();
    }

    private Map<String,Boolean> processList() throws IOException {
        int size = inputStream.readInt();
        // -1 means that directory does not exists in server TODO: return null in that case?
        if (size == -1) {
            return Collections.emptyMap();
        } else {
            Map<String, Boolean> res = new HashMap<>();
            for (int i = 0; i < size; ++i) {
                String name = inputStream.readUTF();
                Boolean isDir = inputStream.readBoolean();
                res.put(name, isDir);
            }
            return res;
        }
    }

    private File processGet(String path) throws IOException {
        long size = inputStream.readLong();
        if (size == 0) {
            return null;
        } else {
            File result = new File(path);
            //noinspection ResultOfMethodCallIgnored
            result.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(result);

            byte[] buffer = new byte[1024];

            for (int i = 0, len; i < size; i += len) {
                len = (int) (size - i > buffer.length ? buffer.length : size - i);
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, len);
                fileOutputStream.write(buffer, 0, len);
            }

            fileOutputStream.flush();
            fileOutputStream.close();
            return result;
        }
    }
}
