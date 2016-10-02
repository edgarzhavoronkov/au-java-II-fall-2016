package ru.spbau.mit.io;

import ru.spbau.mit.model.Repository;
import ru.spbau.mit.exceptions.ReadFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Эдгар on 01.10.2016.
 * Class for deserializing {@link Repository} from {@link InputStream}
 */
public class RepositoryReader {
    public static Repository read(InputStream in) throws ReadFailedException{
        try (ObjectInputStream objectInputStream = new ObjectInputStream(in)) {
            return (Repository) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new ReadFailedException(e);
        }
    }
}
