package ru.spbau.mit.torrent.client;

import ru.spbau.mit.torrent.exceptions.*;
import ru.spbau.mit.torrent.tracker.Tracker;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Эдгар on 30.10.2016.
 * Primitive REPL for client
 */
public class ClientMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ClientMain <tracker ip> <port>");
            System.exit(-1);
        }

        try {
            InetSocketAddress trackerAddress = new InetSocketAddress(InetAddress.getByName(args[0]), Tracker.TRACKER_PORT);
            int port = Integer.parseInt(args[1]);
            Client client = new Client(trackerAddress);

            client.start(port);

            Scanner scanner = new Scanner(System.in);
            printUsage();
            while (true) {
                String input = scanner.nextLine();
                String[] splitInput = input.split("\\s+");
                switch (splitInput[0]) {
                    case "list": {
                        List<FileInfo> res = client.executeList();
                        System.out.println("Id:\tName:\tSize:");
                        for (FileInfo file : res) {
                            System.out.println(file.getFileId() + '\t' + file.getName() + '\t' + file.getSize());
                        }
                        break;
                    }

                    case "upload": {
                        String filename;
                        try {
                            filename = splitInput[1];
                            client.executeUpload(filename);
                            System.out.println("Uploaded file " + filename);
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
                        client.stop();
                        System.exit(0);
                    }

                    default: {
                        printUsage();
                    }
                }
            }
            // look at dis fucking bunch of exceptions
        } catch (UnknownHostException
                | ClientStopFailedException
                | UploadFailException
                | FileNotFoundException
                | ClientStartFailException
                | ListFailException
                | GetFailException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: list | upload <file_path> | get <file_id> <part_num> | exit");
    }
}
