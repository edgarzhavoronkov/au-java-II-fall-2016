package ru.spbau.mit.model.core;

import ru.spbau.mit.command.Command;
import ru.spbau.mit.command.CommandProvider;
import ru.spbau.mit.exceptions.CommandFailException;
import ru.spbau.mit.exceptions.CoreException;
import ru.spbau.mit.exceptions.InitFailedException;
import ru.spbau.mit.io.VcsSerializer;

import java.io.File;
import java.io.IOException;

/**
 * Created by Эдгар on 04.10.2016.
 * Main class for VCS. Inits everything and writes {@link VcsCore} into file for further usage
 */
public class VCS {
    private static final String ENV_FILENAME = "env.json";

    private File envFile;
    private VcsCore core;

    /**
     * Entry point
     * @param args
     */
    public static void main(String[] args) {
        try {
            new VCS(args);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private VCS(String[] args) throws IOException {
        core = readEnvFromFile();
        try {
            executeCommand(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        if (core != null) {
            VcsSerializer.write(core, envFile);
        }
    }

    private VcsCore readEnvFromFile() throws IOException {
        File parentDir = new File(System.getProperty("user.dir"));
        File vcsDir = new File(parentDir, VcsCore.VCS_FOLDER_NAME);
        while (!vcsDir.exists()) {
            String parent = parentDir.getParent();
            if (parent == null) {
                return null;
            }
            parentDir = new File(parent);
            vcsDir = new File(parentDir, VcsCore.VCS_FOLDER_NAME);
        }
        envFile = new File(vcsDir, ENV_FILENAME);
        return VcsSerializer.read(envFile);
    }

    private void executeCommand(String[] args) throws InitFailedException {
        if (args.length == 0) {
            System.out.println("No command specified!");
            System.exit(-1);
        } else {
            if (args[0].equals("init")) {
                if (core != null) {
                    throw new InitFailedException("Repository has already been init");
                }
                try {
                    init();
                } catch (CoreException e) {
                    throw new InitFailedException(e);
                }
            } else {
                if (core == null) {
                    throw new InitFailedException("Repository has not been init!");
                }
                Command cmd = CommandProvider.forName(args[0]);
                if (cmd != null) {
                    String[] arguments = new String[args.length - 1];
                    System.arraycopy(args, 1, arguments, 0, args.length - 1);
                    try {
                        String output = cmd.execute(core, arguments);
                        System.out.println(output);
                    } catch (CommandFailException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Unknown command! Please refer to readme");
                }
            }
        }
    }

    private void init() throws CoreException {
        new File(VcsCore.VCS_FOLDER_NAME).mkdirs();
        envFile = new File(VcsCore.VCS_FOLDER_NAME, ENV_FILENAME);
        core = new VcsCore();
        core.commit("Dummy init");
    }
}
