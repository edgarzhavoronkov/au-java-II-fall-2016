package ru.spbau.mit.client;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Эдгар on 08.10.2016.
 * Main class for client with REPL
 */
public class ClientMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: SimpleServer hostname port");
            System.exit(-1);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        SimpleClient client = new SimpleClient();

        try {
            client.connect(hostname, port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Simple client. Supports get and list requests.\nUsage:\n\t* `get $source $target`." +
                " Gets file $source on server, downloads it to $target\n\t" +
                "* `list $dir`. Lists all files and directories in $dir on server.");
        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine();
            String[] split = input.split("\\s+");

            switch (split[0]) {
                case "list" :
                    try {
                        Map<String, Boolean> result = client.executeList(split[1]);
                        if (result != null) {
                            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                                System.out.println(entry.getKey() + ": " + entry.getValue());
                            }
                        } else {
                            System.out.println("No such directory on server!");
                        }
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case "get" :
                    try {
                        client.executeGet(split[1], split[2]);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case "exit" :
                    try {
                        client.disconnect();
                        System.exit(0);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.exit(-1);
                    }
                    break;

                default:
                    System.err.println("Unknown request!");
                    break;
            }
        }
    }
}
