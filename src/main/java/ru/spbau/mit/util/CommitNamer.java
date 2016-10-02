package ru.spbau.mit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class CommitNamer {
    public static String getNewName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
