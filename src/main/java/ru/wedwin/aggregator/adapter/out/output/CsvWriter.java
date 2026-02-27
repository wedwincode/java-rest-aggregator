package ru.wedwin.aggregator.adapter.out.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.domain.model.out.WriteMode;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// todo append
public class CsvWriter implements OutputWriter {

    @Override
    public String id() {
        return "csv";
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        List<Map<String, String>> rows = flattenRecords(records);
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent()); // todo нужно ли
            }
            switch (spec.mode()) {
                case NEW -> newRows(rows, spec.path());
                case APPEND -> appendRows(rows, spec.path());
                case null, default -> throw new RuntimeException("null!!"); // todo everywhere
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    private static void newRows(List<Map<String, String>> rows, Path file) throws IOException {
        List<String> header = buildHeader(rows);
        writeToFile(header, rows, file, WriteMode.NEW);
    }

    private static void appendRows(List<Map<String, String>> newRows, Path file) throws IOException {
        if (newRows == null || newRows.isEmpty()) {
            return;
        }
        if (!Files.exists(file) || Files.size(file) == 0) {
            newRows(newRows, file);
            return;
        }
        List<String> oldHeader = readHeader(file);
        if (!headerNeedsUpdate(oldHeader, newRows)) {
            writeToFile(oldHeader, newRows, file, WriteMode.APPEND);
            return;
        }
        List<Map<String, String>> oldRows = readAllRows(file, oldHeader);
        List<Map<String, String>> all = new ArrayList<>(oldRows.size() + newRows.size());
        all.addAll(oldRows);
        all.addAll(newRows);
        newRows(all, file);
    }

    private static void writeToFile(List<String> header, List<Map<String, String>> rows, Path file, WriteMode mode) throws IOException {
        StandardOpenOption option;
        CSVFormat format;
        switch (mode) {
            case NEW -> {
                option = StandardOpenOption.TRUNCATE_EXISTING;
                format = CSVFormat.DEFAULT.builder().setHeader(header.toArray(String[]::new)).get();
            }
            case APPEND -> {
                option = StandardOpenOption.APPEND;
                format = CSVFormat.DEFAULT;
            }
            case null, default -> throw new RuntimeException("null!!");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, option);
             CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Map<String, String> row: rows) {
                printer.printRecord(toRecord(header, row));
            }
        }
    }

    private static List<String> readHeader(Path file) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(false).get();
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(r)) {
            Map<String, Integer> header = parser.getHeaderMap();
            if (header == null || header.isEmpty()) {
                throw new IllegalStateException("csv has no header: " + file);
            }
            return new ArrayList<>(header.keySet());
        }
    }

    private static List<String> buildHeader(List<Map<String, String>> rows) {
        Set<String> headerSet = new LinkedHashSet<>();
        for (Map<String, String> row: rows) {
            headerSet.addAll(row.keySet());
        }
        return new ArrayList<>(headerSet);
    }

    private static boolean headerNeedsUpdate(List<String> oldHeader, List<Map<String, String>> newRows) {
        Set<String> set = new LinkedHashSet<>(oldHeader);
        for (Map<String, String> row: newRows) {
            set.addAll(row.keySet());
        }
        return set.size() != oldHeader.size();
    }

    private static List<Map<String, String>> readAllRows(Path file, List<String> header) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader(header.toArray(String[]::new)).setSkipHeaderRecord(true).get();
        try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             CSVParser parser = format.parse(r)) {
            List<Map<String, String>> rows = new ArrayList<>();
            for (CSVRecord record: parser) {
                rows.add(record.toMap()); // todo change if bugs
            }
            return rows;
        }
    }

    private static List<String> toRecord(List<String> header, Map<String, String> row) {
        List<String> record = new ArrayList<>(header.size());
        for (String h: header) {
            record.add(row.getOrDefault(h, ""));
        }
        return record;
    }

    private static List<Map<String, String>> flattenRecords(List<AggregatedRecord> records) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (AggregatedRecord record: records) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("itemId", record.itemId().toString());
            row.put("apiId", record.apiId().toString());
            row.put("timestamp", record.timestamp().toString());
            row.putAll(PayloadMapper.flatten(record.payload()));
            rows.add(row);
        }
        return rows;
    }
}
