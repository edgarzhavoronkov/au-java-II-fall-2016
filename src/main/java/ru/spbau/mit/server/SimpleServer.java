package ru.spbau.mit.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.mit.exceptions.ServerException;
import ru.spbau.mit.util.RequestType;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Эдгар on 08.10.2016.
 * Class for simple server
 * Handles two types of queries
 * 1. List
 * <1 : Int> <path : String>
 * where <b>path</b> - path to a directory on server to show its' list of files
 * reply format:
 * <size : Int> (<name : String <isDir : Boolean>>)*
 * where <b>size</b>- numbers of files and folders in directory
 * <b>name</b> - name of file or folder
 * <b>isDir</b> - flag, indicating whether file is directory or not
 * 2. Get
 * <2 : Int> <path : String>
 * where <b>path</b> - path to a file on server that we want to download
 * with reply format:
 * <size : Long> <content : Bytes>
 * where <b>size</b> - size of a file
 * and <b>content</b> - it' s content
 * if file was not found then server sends back reply with size = 0
 */
public class SimpleServer {
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    private final static Logger log = LogManager.getLogger(SimpleServer.class);

    /**
     * Starts server on given port
     * @param port port to start server on
     * @throws IOException if something fails
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newCachedThreadPool();
        log.info(String.format("Started server on host %s on port number %d"
                , InetAddress.getLocalHost().getHostName()
                , port));
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                log.info(String.format("Accepted connection from %s", socket.getInetAddress()));
                executorService.execute(new ConnectionHandler(socket));
            }
        } catch (IOException e) {
            //throw new ServerException(e);
        }
    }

    /**
     * Stops server
     * @throws IOException if failed to close internal socket
     */
    public void stop() throws IOException {
        serverSocket.close();
        executorService.shutdown();
        log.info("Stopped server");
    }

    private static class ConnectionHandler implements Runnable {
        private final Socket socket;
        private final DataInputStream inputStream;
        private final DataOutputStream outputStream;
        private final static Logger log = LogManager.getLogger(SimpleServer.class);

        ConnectionHandler(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            while (!socket.isClosed() && socket.isConnected()) {
                try {
                    if (inputStream.available() > 0) {
                        RequestType requestType = RequestType.values()[inputStream.readInt()];
                        String path = inputStream.readUTF();
                        log.info(String.format("Received request %s from %s", requestType.toString(), socket.getInetAddress()));
                        switch (requestType) {
                            case LIST : {
                                handleList(path);
                                break;
                            }

                            case GET : {
                                handleGet(path);
                                break;
                            }

                            default : {
                                throw new ServerException("Unknown type of request!");
                            }
                        }
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    //throw new ServerException(e);
                }
            }
            log.info(String.format("Closed connection to %s", socket.getInetAddress()));
        }

        private void handleList(String path) throws IOException {
            File file = new File(path);
            log.info(String.format("Handling list request for directory %s", path));
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    outputStream.writeInt(files.length);
                    for (File f : files) {
                        outputStream.writeUTF(f.getName());
                        outputStream.writeBoolean(f.isDirectory());
                    }
                    log.info(String.format("Successfully processed list request for directory %s", path));
                } else {
                    throw new RuntimeException("Almost impossible happened");
                }
            } else {
                log.info(String.format("Directory %s was not found, sending reply", path));
                outputStream.writeInt(-1);
            }
            //outputStream.flush();
        }

        private void handleGet(String path) throws IOException {
            log.info(String.format("Handling get request for file %s", path));
            File file = new File(path);

            if (file.exists() && !file.isDirectory()) {

                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    outputStream.writeLong(file.length());

                    byte[] bytes = new byte[1024];

                    while (fileInputStream.read(bytes) != -1) {
                        outputStream.write(bytes);
                    }
                    log.info(String.format("Handled successfully get request for file %s", path));
                }
            } else {
                log.info(String.format("No file %s found! Sending zero reply", path));
                outputStream.writeLong(0);
            }
            //outputStream.flush();
        }
    }
}
