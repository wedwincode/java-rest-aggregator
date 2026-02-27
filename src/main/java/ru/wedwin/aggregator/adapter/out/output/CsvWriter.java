package ru.wedwin.aggregator.adapter.out.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.out.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        List<String> header = buildHeader(rows);
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent()); // todo нужно ли
            }
            writeHeaderAndRows(header, rows, spec.path());
        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }
    }

    private static List<String> buildHeader(List<Map<String, String>> rows) {
        Set<String> headerSet = new LinkedHashSet<>();
        for (Map<String, String> row: rows) {
            headerSet.addAll(row.keySet());
        }
        return new ArrayList<>(headerSet);
    }

    private static List<String> toRecord(List<String> header, Map<String, String> row) {
        List<String> record = new ArrayList<>(header.size());
        for (String h: header) {
            record.add(row.getOrDefault(h, ""));
        }
        return record;
    }

    private static void writeHeaderAndRows(List<String> header, List<Map<String, String>> rows, Path path) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader(header.toArray(String[]::new)).get();
        try (BufferedWriter writer = Files.newBufferedWriter(path);
             CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Map<String, String> row: rows) {
                printer.printRecord(toRecord(header, row));
            }
        }
    }

    private static boolean headerNeedsUpdate(List<String> oldHeader, List<Map<String, String>> newRows) {
        Set<String> set = new LinkedHashSet<>(oldHeader);
        for (Map<String, String> row: newRows) {
            set.addAll(row.keySet());
        }
        return set.size() != oldHeader.size();
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
