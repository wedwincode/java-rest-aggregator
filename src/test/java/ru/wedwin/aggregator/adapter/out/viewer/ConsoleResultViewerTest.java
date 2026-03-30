package ru.wedwin.aggregator.adapter.out.viewer;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.app.port.out.Codec;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsoleResultViewerTest {

    @Test
    void givenEmptyFile_whenAll_thenPrintsNothing() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        ConsoleResultViewer viewer = new ConsoleResultViewer(registry);

        Path file = Files.createTempFile("viewer-all-empty", ".txt");
        OutputSpec spec = new OutputSpec(file, new CodecId("json"), WriteMode.NEW);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        try {
            viewer.all(spec);
            assertEquals("", out.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOut);
            Files.deleteIfExists(file);
        }
    }

    @Test
    void givenItemsFromMultipleApis_whenByApi_thenWritesOnlyFilteredItems() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);
        ConsoleResultViewer viewer = new ConsoleResultViewer(registry);

        ApiId targetApiId = new ApiId("api1");
        ApiId anotherApiId = new ApiId("api2");
        CodecId codecId = new CodecId("json");

        Path file = Files.createTempFile("viewer-by-api", ".txt");
        Files.writeString(file, "dummy", StandardCharsets.UTF_8);

        OutputSpec spec = new OutputSpec(file, codecId, WriteMode.NEW);

        AggregatedItem targetItem = new AggregatedItem(
                UUID.randomUUID(),
                targetApiId,
                Instant.now(),
                new Payload.PInt(1)
        );
        AggregatedItem anotherItem = new AggregatedItem(
                UUID.randomUUID(),
                anotherApiId,
                Instant.now(),
                new Payload.PInt(2)
        );

        when(registry.get(codecId)).thenReturn(codec);
        when(codec.read(any(Reader.class))).thenReturn(List.of(targetItem, anotherItem));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        try {
            viewer.byApi(spec, targetApiId);

            verify(registry).get(codecId);
            verify(codec).read(any(Reader.class));
            verify(codec).write(eq(List.of(targetItem)), any(Writer.class));
        } finally {
            System.setOut(originalOut);
            Files.deleteIfExists(file);
        }
    }

    @Test
    void givenEmptyFile_whenByApi_thenDoesNothing() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);
        ConsoleResultViewer viewer = new ConsoleResultViewer(registry);

        Path file = Files.createTempFile("viewer-by-api-empty", ".txt");
        OutputSpec spec = new OutputSpec(file, new CodecId("json"), WriteMode.NEW);

        viewer.byApi(spec, new ApiId("api1"));

        verify(registry).get(any());
        verify(codec, never()).read(any());
        verify(codec, never()).write(any(), any());

        Files.deleteIfExists(file);
    }

    @Test
    void givenApiId_whenProgress_thenPrintsMessage() {
        CodecRegistry registry = mock(CodecRegistry.class);
        ConsoleResultViewer viewer = new ConsoleResultViewer(registry);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        try {
            viewer.progress(new ApiId("api1"));

            assertEquals(
                    "Got response for API: api1" + System.lineSeparator(),
                    out.toString(StandardCharsets.UTF_8)
            );
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void givenError_whenError_thenPrintsMessage() {
        CodecRegistry registry = mock(CodecRegistry.class);
        ConsoleResultViewer viewer = new ConsoleResultViewer(registry);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        try {
            viewer.error(new RuntimeException("boom"));

            assertEquals(
                    "Error: boom" + System.lineSeparator(),
                    out.toString(StandardCharsets.UTF_8)
            );
        } finally {
            System.setOut(originalOut);
        }
    }
}