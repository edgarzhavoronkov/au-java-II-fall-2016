package ru.spbau.mit.torrent.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эдгар on 05.12.2016.
 * Bicycle version of BiConsumer<> which throws checked exception
 */
public interface ClientTask {
    void execute(DataInputStream input, DataOutputStream output) throws IOException;
}
