package ru.spbau.mit;

import ru.spbau.mit.repository.Repository;
import ru.spbau.mit.util.Hasher;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by Эдгар on 25.09.2016.
 * The main class for vcs.
 * Creates a repository directory with default branch 'def'
 * and runs REPL prompt
 */
public class Main {
    public static void main(String[] args) {

        System.out.println(Hasher.getHash(String.valueOf(System.currentTimeMillis())));

        System.out.println("Hello!");
        try {
            Repository repo = new Repository(".repo");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(">> ");
                String input = scanner.nextLine();
                if ("exit".equals(input)) {
                    System.exit(0);
                }
                String output = repo.execute(input);
                System.out.println(output);
            }
        } catch (Exception e) {
            System.out.println("Something gone wrong! Probably your JVM has no right to create folders or so");
        }
    }
}
