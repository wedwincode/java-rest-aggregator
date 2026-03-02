package ru.wedwin.aggregator.adapter.out.codec;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.api.ApiId;
import ru.wedwin.aggregator.domain.model.result.AggregatedItem;
import ru.wedwin.aggregator.domain.model.codec.CodecId;
import ru.wedwin.aggregator.domain.model.result.Payload;
import ru.wedwin.aggregator.port.out.Codec;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// todo append
public class CsvCodec implements Codec {

    private static final Logger log = LogManager.getLogger(CsvCodec.class);

    @Override
    public CodecId id() {
        return new CodecId("csv");
    }

    @Override
    public List<AggregatedItem> read(Reader r) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(false).get();
        CSVParser parser = format.parse(r);

        Map<String, Integer> headerMap = parser.getHeaderMap();
        if (headerMap == null || headerMap.isEmpty()) {
            throw new IllegalStateException("csv has no header");
        }
        log.info(headerMap);

        List<AggregatedItem> items = new ArrayList<>();
        for (CSVRecord record : parser) {
            Map<String, String> row = record.toMap();
            items.add(toItem(row));
        }
        log.info(items);
        return items;
    }

    @Override
    public void write(List<AggregatedItem> items, Writer w) throws IOException {
        List<Map<String, String>> rows = flattenItems(items);
        List<String> header = buildHeader(rows);
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader(header.toArray(String[]::new)).get();
        CSVPrinter printer = new CSVPrinter(w, format);
        for (Map<String, String> row : rows) {
            printer.printRecord(toRecord(header, row));
        }
        printer.flush();
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

    private static List<Map<String, String>> flattenItems(List<AggregatedItem> items) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (AggregatedItem item: items) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("itemId", item.itemId().toString());
            row.put("apiId", item.apiId().toString());
            row.put("fetchedAt", item.fetchedAt().toString());
            row.putAll(PayloadMapper.flatten(item.payload()));
            rows.add(row);
        }
        return rows;
    }

    private static AggregatedItem toItem(Map<String, String> row) {
        UUID itemId = UUID.fromString(required(row, "itemId"));
        ApiId apiId = new ApiId(required(row, "apiId"));
        Instant fetchedAt = Instant.parse(required(row, "fetchedAt"));

        Map<String, String> payloadFlat = new LinkedHashMap<>(row);
        payloadFlat.remove("itemId");
        payloadFlat.remove("apiId");
        payloadFlat.remove("fetchedAt");

        Payload payload = PayloadMapper.unflatten(payloadFlat);
        return new AggregatedItem(itemId, apiId, fetchedAt, payload);
    }

    private static String required(Map<String, String> row, String key) {
        String v = row.get(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("csv row missing required column: " + key);
        }
        return v;
    }
}
