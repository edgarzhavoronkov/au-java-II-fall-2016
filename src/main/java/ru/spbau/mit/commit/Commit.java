package ru.spbau.mit.commit;

import java.util.List;

/**
 * Created by Эдгар on 25.09.2016.
 */
public class Commit {
    private final String number;
    private final String message;
    private final List<String> files;


    public Commit(String number, String message, List<String> files) {
        this.number = number;
        this.message = message;
        this.files = files;
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getFiles() {
        return files;
    }
}
