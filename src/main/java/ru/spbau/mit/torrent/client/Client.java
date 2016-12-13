package ru.spbau.mit.torrent.client;

import lombok.AllArgsConstructor;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Эдгар on 30.10.2016.
 * Concrete implementation of abstract server for client
 * Basically in terms of handling requests
 */
@Log4j2
public class Client extends AbstractServer {
    private Map<Long, TorrentFile> files;
    private final InetSocketAddress trackerAddress;
    private final String workingDir;

    /**
     * Creates client with given tracker address
     * @param trackerAddress - address of torrent tracker
     */
    public Client(InetSocketAddress trackerAddress) {
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
    @Override
    public void start(int port) throws ClientStartFailException {
        try {
            super.start(port);
            files = ClientSerializer.loadFiles(workingDir);
            sendUpdate();
        } catch (ServerStartFailException | RequestSendFailException | SerializationException e) {
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

    /**
     * Stops client. Dumps it's state on disk, then stops all
     * server stuff
     * @throws ClientStopFailedException - if serialization of client's
     * state failed or something gone wrong in halting of server stuff
     */
    @Override
    public void stop() throws ClientStopFailedException {
        try {
            ClientSerializer.saveFiles(workingDir, files);
            super.stop();
        } catch (ServerStopFailException | SerializationException e) {
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

    /**
     * Uploads file for seeding
     * @param filename path to file
     * @throws FileNotFoundException if file doesn't exist
     * @throws UploadFailException if fails to send request to tracker
     */
    public void executeUpload(String filename) throws FileNotFoundException, UploadFailException {
        executeUpload(new File(workingDir, filename));
    }

    /**
     * Uploads file for seeding. Needed for UI, since user can upload file
     * that can lay beneath the root directory
     * @param file File to upload
     * @throws FileNotFoundException if file doesn't exist
     * @throws UploadFailException if fails to send request to tracker
     */
    public void executeUpload(File file) throws UploadFailException, FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        long size = file.length();
        try {
            sendRequest(trackerAddress, (input, output) -> {
                output.writeInt(TrackerRequest.UPLOAD.ordinal());
                output.writeUTF(file.getName());
                output.writeLong(size);

                long id = input.readLong();
                files.put(id, TorrentFile.full(file, size, id));
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
                    getFile(fileInfo, new File(workingDir, fileInfo.getName()), null);
                    return;
                }
            }
            throw new FileNotFoundException("No file with id " + fileId);
        } catch (LoadFailException | NoSeedsFoundException | ListFailException e) {
            log.warn(e.getMessage());
            throw new GetFailException(e);
        }
    }

    /**
     * Getter for UI needs
     * @return all clients' files, downloading and seeding
     */
    public Collection<TorrentFile> getFiles() {
        return files.values();
    }

    /**
     * Another get for UI needs
     * @param info - information about file to get
     * @param target - file to write to
     * @throws NoSeedsFoundException - if no seeds found
     * @throws LoadFailException if loading failed
     */
    public void getFile(FileInfo info, File target, OnChunkDownload handler) throws NoSeedsFoundException, LoadFailException {
        TorrentFile torrentFile = files.get(info.getFileId());
        if (torrentFile == null) {
            torrentFile = TorrentFile.empty(info, target);
            files.put(info.getFileId(), torrentFile);
        }
        if (torrentFile.isFull()) {
            return;
        }
        loadFile(torrentFile, handler);
    }

    private void loadFile(TorrentFile torrentFile, OnChunkDownload handler) throws NoSeedsFoundException, LoadFailException {
        List<InetSocketAddress> seeds = getSeeds(torrentFile.getFileID());
        if (seeds.isEmpty()) {
            throw new NoSeedsFoundException(torrentFile.getFile().getName());
        }

        List<Future<?>> result = seeds
                .stream()
                .map(seed -> super.service.submit(new DownloadHandler(seed, torrentFile, handler)))
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
            sendRequest(trackerAddress, (input, output) -> {
                output.writeInt(TrackerRequest.SOURCES.ordinal());
                output.writeLong(fileID);
                int count = input.readInt();
                for (int i = 0; i < count; ++i) {
                    byte[] address = new byte[4];
                    //noinspection ResultOfMethodCallIgnored
                    input.read(address);
                    int port = input.readInt();
                    InetAddress inetAddress = InetAddress.getByAddress(address);
                    InetSocketAddress seedAddress = new InetSocketAddress(inetAddress, port);
                    if (port != super.localPort || inetAddress.equals(InetAddress.getLoopbackAddress())) {
                        res.add(seedAddress);
                    }
                }
            });
        } catch (RequestSendFailException e) {
            throw new NoSeedsFoundException("Failed to send request to find seeds");
        }
        return res;
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
                FileManager.readChunk(files.get(fileID), chunk, out);
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

    /**
     * Small handler for downloading
     */
    @AllArgsConstructor
    private final class DownloadHandler implements Runnable {
        private final InetSocketAddress seed;
        private final TorrentFile torrentFile;
        private final OnChunkDownload handler;

        /**
         * Sends request to seed and does
         * all the job for downloading the file
         */
        @Override
        public void run() {
            try {
                sendRequest(seed, (input, output) -> {
                    List<Integer> chunks = getChunks(input, output);

                    for (Integer chunk : chunks) {
                        if (!torrentFile.getChunksInProgress().contains(chunk) && !torrentFile.getChunks().contains(chunk)) {
                            torrentFile.startDownload(chunk);
                            loadChunk(chunk, input, output);
                            if (handler != null) {
                                handler.fire(torrentFile.getChunks().size(), torrentFile.chunksCount());
                            }
                        }
                    }
                    try {
                        sendUpdate();
                    } catch (RequestSendFailException e) {
                        throw new IOException(e);
                    }
                });
            } catch (RequestSendFailException e) {
                throw new DownloadFailException(e);
            }
        }

        private void loadChunk(int chunk, DataInputStream input, DataOutputStream output) throws IOException {
            output.writeInt(ClientRequest.GET.ordinal());
            output.writeLong(torrentFile.getFileID());
            output.writeInt(chunk);
            FileManager.writeChunk(torrentFile, chunk, input);
            torrentFile.addChunk(chunk);
        }

        private List<Integer> getChunks(DataInputStream input, DataOutputStream output) throws IOException {
            List<Integer> res = new ArrayList<>();
            output.writeInt(ClientRequest.STAT.ordinal());
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
