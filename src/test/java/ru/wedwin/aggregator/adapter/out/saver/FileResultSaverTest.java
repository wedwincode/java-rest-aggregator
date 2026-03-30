package ru.wedwin.aggregator.adapter.out.saver;

import org.junit.jupiter.api.Test;
import ru.wedwin.aggregator.app.codec.CodecRegistry;
import ru.wedwin.aggregator.app.port.out.Codec;
import ru.wedwin.aggregator.domain.api.ApiId;
import ru.wedwin.aggregator.domain.codec.CodecId;
import ru.wedwin.aggregator.domain.config.OutputSpec;
import ru.wedwin.aggregator.domain.config.WriteMode;
import ru.wedwin.aggregator.domain.result.AggregatedItem;
import ru.wedwin.aggregator.domain.result.Payload;
import tools.jackson.core.exc.StreamReadException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FileResultSaverTest {

    @Test
    void givenNewMode_whenSave_thenWritesItems() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);

        when(registry.get(any())).thenReturn(codec);

        FileResultSaver saver = new FileResultSaver(registry);
        Path file = Files.createTempFile("save-new", ".txt");
        List<AggregatedItem> items = List.of(item(1));
        saver.save(new OutputSpec(file, new CodecId("json"), WriteMode.NEW), items);

        verify(codec).write(eq(items), any(Writer.class));
    }

    @Test
    void givenAppendModeAndExistingFile_whenSave_thenMergesAndWrites() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);

        when(registry.get(any())).thenReturn(codec);

        FileResultSaver saver = new FileResultSaver(registry);

        Path file = Files.createTempFile("save-append", ".txt");
        Files.writeString(file, "existing");

        AggregatedItem existingItem = item(1);
        AggregatedItem newItem = item(2);

        when(codec.read(any(Reader.class))).thenReturn(List.of(existingItem));

        saver.save(new OutputSpec(file, new CodecId("json"), WriteMode.APPEND), List.of(newItem));

        verify(codec).read(any(Reader.class));
        verify(codec).write(eq(List.of(existingItem, newItem)), any(Writer.class));
    }

    @Test
    void givenAppendModeAndEmptyFile_whenSave_thenWritesOnlyNewItems() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);

        when(registry.get(any())).thenReturn(codec);

        FileResultSaver saver = new FileResultSaver(registry);
        Path file = Files.createTempFile("save-empty", ".txt");
        List<AggregatedItem> newItems = List.of(item(1));
        saver.save(new OutputSpec(file, new CodecId("json"), WriteMode.APPEND), newItems);

        verify(codec, never()).read(any());
        verify(codec).write(eq(newItems), any(Writer.class));
    }

    @Test
    void givenNestedPath_whenSave_thenCreatesDirectories() throws Exception {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);

        when(registry.get(any())).thenReturn(codec);

        FileResultSaver saver = new FileResultSaver(registry);
        Path dir = Files.createTempDirectory("nested");
        Path file = dir.resolve("sub/dir/output.txt");
        saver.save(new OutputSpec(file, new CodecId("json"), WriteMode.NEW), List.of(item(1)));

        assert(Files.exists(file));
    }

    @Test
    void givenCodecThrowsStreamReadException_whenSave_thenWrapsException() throws IOException {
        CodecRegistry registry = mock(CodecRegistry.class);
        Codec codec = mock(Codec.class);

        when(registry.get(any())).thenReturn(codec);
        doThrow(new StreamReadException(null, "bad")).when(codec).write(any(), any());

        FileResultSaver saver = new FileResultSaver(registry);

        Path file;
        try {
            file = Files.createTempFile("error", ".txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThrows(ResultSaveException.class,
                () -> saver.save(new OutputSpec(file, new CodecId("json"), WriteMode.NEW), List.of(item(1))));
    }

    private static AggregatedItem item(int value) {
        return new AggregatedItem(
                UUID.randomUUID(),
                new ApiId("api"),
                Instant.now(),
                new Payload.PInt(value)
        );
    }
}