package ru.spbau.mit.torrent.tracker;

import ru.spbau.mit.torrent.exceptions.SerializationException;
import ru.spbau.mit.torrent.exceptions.TrackerStartFailException;
import ru.spbau.mit.torrent.exceptions.TrackerStopFailException;

import java.util.Scanner;

/**
 * Created by Эдгар on 30.10.2016.
 * REPL class for tracker
 */
public class TrackerMain {
    public static void main(String[] args) {
        Tracker tracker = null;
        try {
            tracker = new Tracker(System.getProperty("user.dir"));
        } catch (SerializationException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        Tracker finalTracker = tracker;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                finalTracker.stop();
            } catch (TrackerStopFailException e) {
                System.err.println(e.getMessage());
            }
        }));

        try {
            tracker.start();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("exit".equals(input)) {
                    tracker.stop();
                    return;
                }
            }
        } catch (TrackerStartFailException | TrackerStopFailException e) {
            System.err.println(e.getMessage());
        }
    }
}
