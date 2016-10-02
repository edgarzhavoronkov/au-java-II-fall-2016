package ru.spbau.mit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Эдгар on 01.10.2016.
 * Small class that provides new names for commits
 * Does literally noting else
 */
public class CommitNameProvider {
    public static String getNewName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
