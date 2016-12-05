package ru.spbau.mit.torrent.tracker;

import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.common.AbstractServer;
import ru.spbau.mit.torrent.exceptions.*;
import ru.spbau.mit.torrent.io.TrackerSerializer;
import ru.spbau.mit.torrent.utils.ClientInfo;
import ru.spbau.mit.torrent.utils.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 30.10.2016.
 */
@Log4j2
public class Tracker extends AbstractServer {
    public static final int TRACKER_PORT = 8081;

    private long maxID;
    private final String workingDir;
    private final List<FileInfo> files;
    private final Set<ClientInfo> clients = new HashSet<>();


    public Tracker(String workingDir) throws SerializationException {
        log.info("Creating tracker");
        this.workingDir = workingDir;
        files = TrackerSerializer.loadFiles(workingDir);
        maxID = files.isEmpty() ? 0 : files.get(files.size() - 1).getFileId();
    }

    public void start() throws TrackerStartFailException {
        try {
            super.start(TRACKER_PORT);
        } catch (ServerStartFailException e) {
            throw new TrackerStartFailException(e);
        }
    }

    @Override
    public void stop() throws TrackerStopFailException {
        try {
            TrackerSerializer.saveFiles(workingDir, files);
            super.stop();
        } catch (SerializationException | ServerStopFailException e) {
            throw new TrackerStopFailException(e);
        }
    }

    @Override
    protected void handleRequest(InetAddress inetAddress, DataInputStream in, DataOutputStream out) throws IOException {
        TrackerRequest request = TrackerRequest.values()[in.readInt()];
        switch (request) {
            case LIST : {
                log.info("Handling list request");
                out.writeInt(files.size());
                for (FileInfo file : files) {
                    out.writeLong(file.getFileId());
                    out.writeUTF(file.getName());
                    out.writeLong(file.getSize());
                }
                break;
            }

            case SOURCES: {
                log.info("Handling sources request");
                long fileID = in.readLong();
                List<ClientInfo> sources = clients
                        .stream()
                        .filter(client -> client.hasFile(fileID) && client.isActive())
                        .collect(Collectors.toList());
                out.writeInt(sources.size());
                for (ClientInfo client : sources) {
                    out.write(client.getAddress());
                    out.writeInt(client.getPort());
                }
                break;
            }

            case UPDATE: {
                log.info("Handling update request");
                int port = in.readInt();
                ClientInfo client = new ClientInfo(inetAddress.getAddress(), port);
                clients.remove(client);
                int count = in.readInt();
                for (int i =0; i < count; ++i) {
                    long fileID = in.readLong();
                    client.addFile(fileID);
                }
                clients.add(client);
                out.writeBoolean(true);

                break;
            }

            case UPLOAD: {
                log.info("Handling upload request");
                long newID = maxID + 1;
                maxID = newID;
                String name = in.readUTF();
                long size = in.readLong();
                files.add(new FileInfo(newID, name, size));
                out.writeLong(newID);
                break;
            }
        }
    }
}
