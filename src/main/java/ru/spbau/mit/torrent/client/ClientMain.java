package ru.spbau.mit.torrent.client;

import ru.spbau.mit.torrent.tracker.Tracker;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
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
                        List<FileInfo> res = client.executeList();
                        System.out.println("Id:\tFileName:\tSize:");
                        for (FileInfo file : res) {
                            System.out.println(file.getFileId() + '\t' + file.getName() + '\t' + file.getSize());
                        }
                        break;
                    }

                    case "upload": {
                        String filename;
                        try {
                            filename = splitInput[1];
                            long id = client.executeUpload(filename);
                            System.out.println("Uploaded file " + filename + " with id=" + id);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.err.println(e.getMessage());
                            printUsage();
                        }
                        break;
                    }

                    case "get": {
                        Long fileId;
                        Integer partNum;
                        try {
                            fileId = Long.parseLong(splitInput[1]);
                            partNum = Integer.parseInt(splitInput[2]);
                            DataInputStream dis = client.executeGet(fileId, partNum);
                            //TODO: save bytes from part into corresponding file info
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

                    default: {
                        printUsage();
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: list | upload <file_path> | get <file_id> <part_num> | exit");
    }
}
