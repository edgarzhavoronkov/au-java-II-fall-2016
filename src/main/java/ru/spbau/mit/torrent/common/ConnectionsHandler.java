package ru.spbau.mit.torrent.common;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.client.ClientTask;
import ru.spbau.mit.torrent.exceptions.ConnectionHandlerStartFailException;
import ru.spbau.mit.torrent.exceptions.ConnectionHandlerStopFailException;
import ru.spbau.mit.torrent.exceptions.RequestSendFailException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Эдгар on 04.12.2016.
 * Module for handling incoming connections
 * Since client and tracker both do such job
 * Declared abstract since i don't want to mix
 * client and tracker requests in one piece
 */
@Log4j2
public abstract class ConnectionsHandler implements Runnable {
    private ServerSocket serverSocket;
    @Getter
    private int localPort;
    @Getter
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    /**
     * Starts module on given port
     * @param port port number
     * @throws ConnectionHandlerStartFailException if start failed
     */
    public void start(int port) throws ConnectionHandlerStartFailException {
        log.info("Started connection handler at port " + port);
        localPort = port;
        executor.submit(this);
    }

    /**
     * Shuts module down
     * @throws ConnectionHandlerStopFailException if failed
     */
    public void stop() throws ConnectionHandlerStopFailException {
        log.info("Stopping connection handler");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new ConnectionHandlerStopFailException(e);
            } finally {
                serverSocket = null;
            }
        }
        executor.shutdown();
    }

    /**
     * Does all the job with handling connections
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!serverSocket.isClosed()) {
            try {
                Socket incoming = serverSocket.accept();
                executor.submit(() -> handleConnection(incoming));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    log.info("Server is closed");
                    return;
                }
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Used to send requests between network agents(tracker and clients)
     * @param address address of agent
     * @param task callback for handling data in requests
     * @throws RequestSendFailException if fails
     */
    public void sendRequest(InetSocketAddress address, ClientTask task) throws RequestSendFailException {
        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            task.execute(input, output);
        } catch (IOException e) {
            throw new RequestSendFailException(e);
        }
    }

    private void handleConnection(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            while (socket.isConnected() && !socket.isClosed()) {
                handleRequest(socket.getInetAddress(), dis, dos);
                dos.flush();
            }
        } catch (SocketException | EOFException e) {
            log.info("Disconnected");
        } catch (IOException e) {
            log.warn("Error in connection");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.warn("Error in closing socket");
            } finally {
                //noinspection UnusedAssignment
                socket = null;
            }
        }
    }

    public abstract void handleRequest(
            InetAddress inetAddress,
            DataInputStream in,
            DataOutputStream out) throws IOException;
}
