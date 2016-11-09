package ru.spbau.mit.torrent.tracker;

/**
 * Created by Эдгар on 30.10.2016.
 */
public class TrackerMain {
    // TODO
    public static void main(String[] args) {
        Tracker tracker = new Tracker();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            tracker.stop();
        }));

        tracker.start();


    }
}
