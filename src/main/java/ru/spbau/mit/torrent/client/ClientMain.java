package ru.spbau.mit.torrent.client;

import ru.spbau.mit.torrent.tracker.Tracker;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Эдгар on 30.10.2016.
 */
public class ClientMain {
    // TODO
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ClientMain <tracker ip> <port>");
            System.exit(-1);
        }

        try {
            InetAddress trackerAddress = InetAddress.getByName(args[0]);
            InetSocketAddress trackerSocketAddress = new InetSocketAddress(trackerAddress, Tracker.trackerPort);
            Client client = new Client();
            client.connect(trackerSocketAddress);

            Scanner scanner = new Scanner(System.in);
            printUsage();
            while (true) {
                String input = scanner.nextLine();
                String[] splitInput = input.split("\\s+");
                switch (splitInput[0]) {
                    case "list": {
                        client.executeList();
                        break;
                    }

                    case "upload": {
                        String filename;
                        try {
                            filename = splitInput[1];
                            client.executeUpload(filename);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println(e.getMessage());
                            printUsage();
                        }
                        break;
                    }

                    case "get": {
                        Long fileId;
                        try {
                            fileId = Long.parseLong(splitInput[1]);
                            client.executeGet(fileId);
                        } catch (IndexOutOfBoundsException e) {
                            System.err.println(e.getMessage());
                            printUsage();
                        } catch (NumberFormatException e) {
                            System.err.println(e.getMessage());
                        }
                        break;
                    }

                    case "exit": {
                        client.disconnect();
                        System.exit(0);
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: list | upload <file_path> | get <file_id> | exit");
    }
}
