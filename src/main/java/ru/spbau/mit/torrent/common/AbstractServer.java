package ru.spbau.mit.torrent.common;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import ru.spbau.mit.torrent.exceptions.ServerStartFailException;
import ru.spbau.mit.torrent.exceptions.ServerStopFailException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Эдгар on 04.12.2016.
 */
@Log4j2
public abstract class AbstractServer implements Runnable {
    protected ServerSocket serverSocket;
    protected int localPort;
    protected final ScheduledExecutorService service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    protected void start(int port) throws ServerStartFailException {
        log.info("Started server at port " + port);
        localPort = port;
        service.submit(this);
    }

    protected void stop() throws ServerStopFailException {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new ServerStopFailException(e);
            } finally {
                serverSocket = null;
            }
        }
        service.shutdown();
    }

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
                service.submit(() -> handleConnection(incoming));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    log.info("Server is closed");
                    return;
                }
                throw new RuntimeException(e);
            }
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

    protected abstract void handleRequest(
            InetAddress inetAddress,
            DataInputStream in,
            DataOutputStream out) throws IOException;
}
