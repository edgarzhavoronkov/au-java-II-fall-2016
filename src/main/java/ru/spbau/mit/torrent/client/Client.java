package ru.spbau.mit.torrent.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.spbau.mit.torrent.common.NetworkRequest;
import ru.spbau.mit.torrent.common.ConnectionsHandler;
import ru.spbau.mit.torrent.exceptions.*;
import ru.spbau.mit.torrent.io.ClientSerializer;
import ru.spbau.mit.torrent.utils.FileInfo;
import ru.spbau.mit.torrent.utils.FileManager;
import ru.spbau.mit.torrent.utils.TorrentFile;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 30.10.2016.
 * Implementation of torrent client
 * I replaced inheritance with composition
 */
@Log4j2
public class Client {
    //test driven design. Don't like it already
    @Getter
    private final ConnectionsHandler connectionsHandler = new ConnectionsHandler() {
        @Override
        public void handleRequest(InetAddress inetAddress, DataInputStream in, DataOutputStream out) throws IOException {
            NetworkRequest request = NetworkRequest.values()[in.readInt()];
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
                    FileManager.readChunk(files.get(fileID), chunk, out);
                    break;
                }
            }
        }
    };

    private Map<Long, TorrentFile> files;
    private final InetSocketAddress trackerAddress;
    private final String workingDir;

    /**
     * Creates client with given tracker address
     * @param trackerAddress - address of torrent tracker
     */
    Client(InetSocketAddress trackerAddress) {
        this(trackerAddress, System.getProperty("user.dir"));
    }

    /**
     * Creates client with given tracker address and given working directory
     * @param trackerAddress - address of torrent tracker
     * @param workingDir - path to working directory
     */
    public Client(InetSocketAddress trackerAddress, String workingDir) {
        this.trackerAddress = trackerAddress;
        this.workingDir = workingDir;
    }

    /**
     * Starts client on given port, reads if's files from disk
     * and sends update request to tracker
     * @param port - port to start on
     * @throws ClientStartFailException if IO problems occurred
     * during state deserialization or sending update request
     * to tracker
     */
    public void start(int port) throws ClientStartFailException {
        try {
            log.info("Starting client at " + port);
            getConnectionsHandler().start(port);
            files = ClientSerializer.loadFiles(workingDir);
            sendUpdate();
            log.info("Client is successfully running");
        } catch (ConnectionHandlerStartFailException | RequestSendFailException | SerializationException e) {
            throw new ClientStartFailException(e);
        }
        getConnectionsHandler().getExecutor().scheduleAtFixedRate(() -> {
            try {
                sendUpdate();
            } catch (RequestSendFailException e) {
                throw new RuntimeException(e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    /**
     * Stops client. Dumps it's state on disk, then stops all
     * server stuff
     * @throws ClientStopFailedException - if serialization of client's
     * state failed or something gone wrong in halting of server stuff
     */
    public void stop() throws ClientStopFailedException {
        try {
            log.info("Stopping client");
            ClientSerializer.saveFiles(workingDir, files);
            getConnectionsHandler().stop();
            log.info("Client successfully stopped");
        } catch (ConnectionHandlerStopFailException | SerializationException e) {
            throw new ClientStopFailedException(e);
        }
    }

    /**
     * Get a list of seeded files from tracker
     * @return list where entries consist of file name, id and size
     * @throws ListFailException if client failed to send list
     * request to server
     */
    public List<FileInfo> executeList() throws ListFailException {
        List<FileInfo> result = new ArrayList<>();
        try {
            getConnectionsHandler().sendRequest(trackerAddress, (input, output) -> {
                log.info("Sending list request");
                output.writeInt(NetworkRequest.LIST.ordinal());

                int count = input.readInt();
                for (int i = 0; i < count; ++i) {
                    long fileID = input.readLong();
                    String name = input.readUTF();
                    long size = input.readLong();
                    result.add(new FileInfo(fileID, name, size));
                }
                log.info("Listed " + count + " file(s)");
            });
            return result;
        } catch (RequestSendFailException e) {
            throw new ListFailException(e);
        }
    }

    /**
     * Uploads file for seeding
     * @param filename path to file
     * @throws FileNotFoundException if file doesn't exist
     * @throws UploadFailException if fails to send request to tracker
     */
    public void executeUpload(String filename) throws FileNotFoundException, UploadFailException {
        File file = new File(workingDir, filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
        long size = file.length();
        try {
            getConnectionsHandler().sendRequest(trackerAddress, (input, output) -> {
                log.info("Sending upload request");
                output.writeInt(NetworkRequest.UPLOAD.ordinal());
                output.writeUTF(filename);
                output.writeLong(size);

                long id = input.readLong();
                files.put(id, TorrentFile.full(file, size, id));
                log.info("Successfully uploaded file " + filename + " with id=" + id);
            });
            sendUpdate();
        } catch (RequestSendFailException e) {
            throw new UploadFailException(e);
        }
    }

    /**
     * Get file with specific id
     * @param fileId file id
     * @throws FileNotFoundException if file is not present
     * @throws GetFailException if fails to send get request to tracker
     */
    public void executeGet(Long fileId) throws FileNotFoundException, GetFailException {
        try {
            for (FileInfo fileInfo : executeList()) {
                if (fileId == fileInfo.getFileId()) {
                    getFile(fileInfo, new File(workingDir, fileInfo.getName()));
                    return;
                }
            }
            throw new FileNotFoundException("No file with id " + fileId);
        } catch (LoadFailException | NoSeedsFoundException | ListFailException e) {
            log.warn(e.getMessage());
            throw new GetFailException(e);
        }
    }

    private void getFile(FileInfo info, File target) throws NoSeedsFoundException, LoadFailException {
        TorrentFile torrentFile = files.get(info.getFileId());
        if (torrentFile == null) {
            torrentFile = TorrentFile.empty(info, target);
            files.put(info.getFileId(), torrentFile);
        }
        if (torrentFile.isFull()) {
            return;
        }
        loadFile(torrentFile);
    }

    private void loadFile(TorrentFile torrentFile) throws NoSeedsFoundException, LoadFailException {
        List<InetSocketAddress> seeds = getSeeds(torrentFile.getFileID());
        if (seeds.isEmpty()) {
            throw new NoSeedsFoundException(torrentFile.getFile().getName());
        }

        List<Future<?>> result = seeds
                .stream()
                .map(seed -> getConnectionsHandler().getExecutor().submit(new DownloadHandler(seed, torrentFile)))
                .collect(Collectors.toList());

        for (Future<?> future : result) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new LoadFailException(e);
            }
        }

        if (!torrentFile.isFull()) {
            throw new LoadFailException("File " + torrentFile.getFile().getName() + " loaded partially");
        }
        log.info("Download is successful");
    }

    private List<InetSocketAddress> getSeeds(long fileID) throws NoSeedsFoundException {
        List<InetSocketAddress> res = new ArrayList<>();
        try {
            getConnectionsHandler().sendRequest(trackerAddress, (input, output) -> {
                output.writeInt(NetworkRequest.SOURCES.ordinal());
                output.writeLong(fileID);
                int count = input.readInt();
                for (int i = 0; i < count; ++i) {
                    byte[] address = new byte[4];
                    //noinspection ResultOfMethodCallIgnored
                    input.read(address);
                    int port = input.readInt();
                    InetAddress inetAddress = InetAddress.getByAddress(address);
                    InetSocketAddress seedAddress = new InetSocketAddress(inetAddress, port);
                    if (port != connectionsHandler.getLocalPort() || inetAddress.equals(InetAddress.getLoopbackAddress())) {
                        res.add(seedAddress);
                    }
                }
            });
        } catch (RequestSendFailException e) {
            throw new NoSeedsFoundException("Failed to send request to find seeds");
        }
        return res;
    }

    private void sendUpdate() throws RequestSendFailException {
        getConnectionsHandler().sendRequest(trackerAddress, (input, output) -> {
            log.info("Sending update request");
            output.writeInt(NetworkRequest.UPDATE.ordinal());
            output.writeInt(connectionsHandler.getLocalPort());
            output.writeInt(files.size());
            for (TorrentFile file : files.values()) {
                output.writeLong(file.getFileID());
            }
            boolean updated = input.readBoolean();
            if (!updated) {
                log.warn("Update failed!");
            }
            log.info("Successfully updated");
        });
    }

    /**
     * Small handler for downloading
     */
    @AllArgsConstructor
    private final class DownloadHandler implements Runnable {
        private final InetSocketAddress seed;
        private final TorrentFile torrentFile;

        /**
         * Sends request to seed and does
         * all the job for downloading the file
         */
        @Override
        public void run() {
            try {
                getConnectionsHandler().sendRequest(seed, (input, output) -> {
                    List<Integer> chunks = getChunks(input, output);

                    for (Integer chunk : chunks) {
                        if (!torrentFile.getChunks().contains(chunk)) {
                            loadChunk(chunk, input, output);
                        }
                    }
                });
            } catch (RequestSendFailException e) {
                throw new DownloadFailException(e);
            }
        }

        private void loadChunk(int chunk, DataInputStream input, DataOutputStream output) throws IOException {
            output.writeInt(NetworkRequest.GET.ordinal());
            output.writeLong(torrentFile.getFileID());
            output.writeInt(chunk);
            FileManager.writeChunk(torrentFile, chunk, input);
            torrentFile.addChunk(chunk);
        }

        private List<Integer> getChunks(DataInputStream input, DataOutputStream output) throws IOException {
            List<Integer> res = new ArrayList<>();
            output.writeInt(NetworkRequest.STAT.ordinal());
            output.writeLong(torrentFile.getFileID());
            int count = input.readInt();
            for (int i = 0; i < count; ++i) {
                int chunk = input.readInt();
                res.add(chunk);
            }
            return res;
        }
    }
}
