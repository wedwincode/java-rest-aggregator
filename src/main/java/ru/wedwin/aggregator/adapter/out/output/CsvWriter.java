package ru.wedwin.aggregator.adapter.out.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CsvWriter implements OutputWriter {

    @Override
    public String id() {
        return "csv";
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        Set<String> headersSet = new LinkedHashSet<>();
        List<Map<String, String>> rows = flattenRecords(records);
        for (Map<String, String> rowMap: rows) {
            headersSet.addAll(rowMap.keySet());
        }
        List<String> headers = new ArrayList<>(headersSet);
        try {
            if (spec.path().getParent() != null) {
                Files.createDirectories(spec.path().getParent()); // todo нужно ли
            }

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(headers.toArray(String[]::new))
                    .get();

            try (BufferedWriter writer = Files.newBufferedWriter(spec.path());
                 CSVPrinter printer = new CSVPrinter(writer, format)) {
                for (Map<String, String> rowMap: rows) {
                    List<String> record = new ArrayList<>(headers.size());
                    for (String header: headers) {
                        record.add(rowMap.getOrDefault(header, ""));
                    }
                    printer.printRecord(record);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("failed to write output to " + spec.path(), e);
        }

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
