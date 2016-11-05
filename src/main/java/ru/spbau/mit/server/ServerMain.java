package ru.spbau.mit.server;

import java.io.IOException;

/**
 * Created by Эдгар on 08.10.2016.
 * Server-side main.
 */
public class ServerMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ServerMain port_number");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        SimpleServer server = new SimpleServer();
        try {
            server.start(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    try {
                        server.stop();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.exit(-1);
                    }
                }
        ));
    }
}
