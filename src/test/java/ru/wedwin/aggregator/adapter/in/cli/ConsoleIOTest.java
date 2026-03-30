package ru.wedwin.aggregator.adapter.in.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsoleIOTest {

    @Test
    void givenString_whenPrintln_thenWritesLine() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(out, true, StandardCharsets.UTF_8)
        );
        io.println("hello");

        assertEquals("hello" + System.lineSeparator(), out.toString(StandardCharsets.UTF_8));
    }

    @Test
    void givenObject_whenPrintln_thenWritesObject() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(out, true, StandardCharsets.UTF_8)
        );
        io.println(123);

        assertEquals("123" + System.lineSeparator(), out.toString(StandardCharsets.UTF_8));
    }

    @Test
    void givenString_whenPrint_thenWritesWithoutNewLine() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(out, true, StandardCharsets.UTF_8)
        );
        io.print("hello");

        assertEquals("hello", out.toString(StandardCharsets.UTF_8));
    }

    @Test
    void givenInputWithSpaces_whenReadLine_thenReturnsTrimmed() {
        ByteArrayInputStream in = new ByteArrayInputStream("   hello   \n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleIO io = new ConsoleIO(
                in,
                new PrintStream(out, true, StandardCharsets.UTF_8)
        );
        String result = io.readLine("prompt: ");

        assertEquals("hello", result);
        assertEquals("prompt: ", out.toString(StandardCharsets.UTF_8));
    }

    @Test
    void givenEmptyInput_whenReadLine_thenReturnsEmptyString() {
        ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleIO io = new ConsoleIO(
                in,
                new PrintStream(out, true, StandardCharsets.UTF_8)
        );
        String result = io.readLine("> ");

        assertEquals("", result);
        assertEquals("> ", out.toString(StandardCharsets.UTF_8));
    }
}