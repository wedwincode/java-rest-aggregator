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

    public void println(Object obj) {
        out.println(obj);
    }

    public void print(String s) {
        out.print(s);
    }

    public String readLine(String promptToShow) {
        print(promptToShow);
        return scanner.nextLine().trim();
    }
}
