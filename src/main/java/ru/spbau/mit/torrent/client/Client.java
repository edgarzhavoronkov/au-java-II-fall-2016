package ru.spbau.mit.torrent.client;

import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.common.AbstractServer;
import ru.spbau.mit.torrent.exceptions.*;
import ru.spbau.mit.torrent.io.ClientSerializer;
import ru.spbau.mit.torrent.tracker.TrackerRequest;
import ru.spbau.mit.torrent.utils.FileInfo;
import ru.spbau.mit.torrent.utils.FileManager;
import ru.spbau.mit.torrent.utils.TorrentFile;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Эдгар on 30.10.2016.
 */
@Log4j2
public class Client extends AbstractServer {
    private final Map<Long, TorrentFile> files;
    private final FileManager fileManager;
    private final InetSocketAddress trackerAddress;
    private final String workingDir;

    public Client(InetSocketAddress trackerAddress) {
        this(trackerAddress, System.getProperty("user,dir"));
    }

    public Client(InetSocketAddress trackerAddress, String workingDir) {
        this.trackerAddress = trackerAddress;
        this.workingDir = workingDir;
        this.files = ClientSerializer.loadFiles(workingDir);
        this.fileManager = new FileManager();
    }

    @Override
    public void start(int port) throws ClientStartFailException {
        try {
            super.start(port);
            sendUpdate();
        } catch (ServerStartFailException | RequestSendFailException e) {
            throw new ClientStartFailException(e);
        }
        super.service.scheduleAtFixedRate(() -> {
            try {
                sendUpdate();
            } catch (RequestSendFailException e) {
                throw new RuntimeException(e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void stop() throws ClientStopFailedException {
        ClientSerializer.saveFiles(workingDir, files);
        try {
            super.stop();
        } catch (ServerStopFailException e) {
            throw new ClientStopFailedException(e);
        }
    }

    public List<FileInfo> executeList() throws ListFailException {
        List<FileInfo> result = new ArrayList<>();
        try {
            sendRequest(trackerAddress, (input, output) -> {
                output.writeInt(TrackerRequest.LIST.ordinal());

                int count = input.readInt();
                for (int i = 0; i < count; ++i) {
                    long fileID = input.readLong();
                    String name = input.readUTF();
                    long size = input.readLong();
                    result.add(new FileInfo(fileID, name, size));
                }
            });
            return result;
        } catch (RequestSendFailException e) {
            throw new ListFailException(e);
        }
    }

    public void executeUpload(String filename) throws FileNotFoundException, UploadFailException {
        File file = new File(workingDir, filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
        long size = file.length();
        try {
            sendRequest(trackerAddress, (input, output) -> {
                output.writeInt(TrackerRequest.UPLOAD.ordinal());
                output.writeUTF(filename);
                output.writeLong(size);

                long id = input.readLong();
                files.put(id, TorrentFile.full(file, size, id));
            });
            sendUpdate();
        } catch (RequestSendFailException e) {
            throw new UploadFailException(e);
        }
    }

    public DataInputStream executeGet(Long fileId, Integer partNum) {
        return null;
    }

    @Override
    protected void handleRequest(InetAddress inetAddress, DataInputStream in, DataOutputStream out) throws IOException {
        ClientRequest request = ClientRequest.values()[in.readInt()];
        switch (request) {
            case STAT: {
                long id = in.readLong();
                out.writeInt(files.get(id).getChunks().size());
                for (Integer chunk : files.get(id).getChunks()) {
                    out.writeInt(chunk);
                }
                break;
            }

            case GET: {
                long fileID = in.readLong();
                int chunk = in.readInt();
                fileManager.readChunk(files.get(fileID), chunk, out);
                break;
            }
        }
    }

    private void sendUpdate() throws RequestSendFailException {
        sendRequest(trackerAddress, (input, output) -> {
            output.writeInt(TrackerRequest.UPDATE.ordinal());
            output.writeInt(super.localPort);
            output.writeInt(files.size());
            for (TorrentFile file : files.values()) {
                output.writeLong(file.getFileID());
            }
            boolean updated = input.readBoolean();
            if (!updated) {
                log.warn("Update failed!");
            }
        });
    }

    private void sendRequest(InetSocketAddress trackerAddress, ClientTask task) throws RequestSendFailException {
        try (Socket socket = new Socket(trackerAddress.getAddress(), trackerAddress.getPort())) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            task.execute(input, output);
        } catch (IOException e) {
            throw new RequestSendFailException(e);
        }
    }
}
