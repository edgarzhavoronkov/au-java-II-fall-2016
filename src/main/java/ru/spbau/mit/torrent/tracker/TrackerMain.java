package ru.spbau.mit.torrent.tracker;

import ru.spbau.mit.torrent.exceptions.TrackerStartFailException;
import ru.spbau.mit.torrent.exceptions.TrackerStopFailException;

/**
 * Created by Эдгар on 30.10.2016.
 */
public class TrackerMain {
    public static void main(String[] args) {
        final Tracker tracker = new Tracker();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tracker.stop();
            } catch (TrackerStopFailException e) {
                System.err.println(e.getMessage());
            }
        }));

        try {
            tracker.start();
            //smth more?
            tracker.stop();
        } catch (TrackerStartFailException | TrackerStopFailException e) {
            System.err.println(e.getMessage());
        }
    }
}
