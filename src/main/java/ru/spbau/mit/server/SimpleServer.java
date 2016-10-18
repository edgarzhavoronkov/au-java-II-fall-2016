package ru.spbau.mit.server;

import java.io.*;
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

    private final Runnable connectionsHandler = () -> {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ConnectionHandler(socket));
            }
        } catch (IOException ignored) {
            // FIXME: 15.10.2016 Is there any proper way to handle this?
            //throw new RuntimeException(e);
        }
    };

    /**
     * Starts server on given port
     * @param port port to start server on
     * @throws IOException if something fails
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newCachedThreadPool();
        executorService.execute(connectionsHandler);
    }

    /**
     * Stops server
     * @throws IOException if failed to close internal socket
     */
    public void stop() throws IOException {
        serverSocket.close();
        executorService.shutdown();
    }

    private static class ConnectionHandler implements Runnable {
        private final Socket socket;
        private final DataInputStream inputStream;
        private final DataOutputStream outputStream;


        ConnectionHandler(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    int request = inputStream.readInt();
                    String path = inputStream.readUTF();

                    switch (request) {
                        case 1 : {
                            handleList(path);
                            break;
                        }

                        case 2 : {
                            handleGet(path);
                            break;
                        }

                        default : {
                            throw new RuntimeException("Unknown type of request!");
                        }
                    }
                } catch (IOException ignored) {
                    // FIXME: 15.10.2016 And here?
                    //throw new RuntimeException(e);
                }
            }
        }

        private void handleList(String path) throws IOException {
            File file = new File(path);

            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                //TODO: NPE warning?
                outputStream.writeInt(files.length);
                for (File f : files) {
                    outputStream.writeUTF(f.getName());
                    outputStream.writeBoolean(f.isDirectory());
                }
            } else {
                outputStream.writeInt(-1);
            }
            outputStream.flush();
        }

        private void handleGet(String path) throws IOException {
            File file = new File(path);

            if (file.exists() && !file.isDirectory()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                outputStream.writeLong(file.length());

                byte[] bytes = new byte[1024];

                while (fileInputStream.read(bytes) != -1) {
                    outputStream.write(bytes);
                }
                fileInputStream.close();
            } else {
                outputStream.writeLong(0);
            }
            outputStream.flush();
        }
    }
}
