package ru.spbau.mit.io;

import ru.spbau.mit.model.Repository;
import ru.spbau.mit.exceptions.WriteFailedException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by Эдгар on 01.10.2016.
 */
public class RepositoryWriter {
    public void write(Repository repo, OutputStream out) throws WriteFailedException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(repo);
        } catch (IOException e) {
            throw new WriteFailedException(e);
        }
    }
}
