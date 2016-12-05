package ru.spbau.mit.torrent.tracker;

import ru.spbau.mit.torrent.exceptions.SerializationException;
import ru.spbau.mit.torrent.exceptions.TrackerStartFailException;
import ru.spbau.mit.torrent.exceptions.TrackerStopFailException;

/**
 * Created by Эдгар on 30.10.2016.
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
            tracker.stop();
        } catch (TrackerStartFailException | TrackerStopFailException e) {
            System.err.println(e.getMessage());
        }
    }
}
