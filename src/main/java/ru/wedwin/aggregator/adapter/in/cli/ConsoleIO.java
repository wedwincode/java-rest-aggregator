package ru.wedwin.aggregator.adapter.in.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleIO {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleIO(InputStream in, PrintStream out) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
    }

    public void println(String s) {
        out.println(s);
    }

    public void print(String s) {
        out.print(s);
    }

    public String readLine(String promptToShow) {
        print(promptToShow);
        return scanner.nextLine().strip();
    }

    public int readInt(String promptToShow, int min, int max) {
        while (true) {
            String s = readLine(promptToShow);
            try {
                int value = Integer.parseInt(s);
                if (value < min || value > max) {
                    println("value is out of range [" + min + ", " + max + "]");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                println("not a number");
            }
        }
    }

    public boolean readDecision(String promptToShow) {
        while (true) {
            String s = readLine(promptToShow + " (y/n): ").strip().toLowerCase();
            if (s.equals("y") || s.equals("yes")) {
                return true;
            }
            if (s.equals("n") || s.equals("no")) {
                return false;
            }
            println("enter \"yes\" (y) or \"no\" (n)");
        }
    }
}
