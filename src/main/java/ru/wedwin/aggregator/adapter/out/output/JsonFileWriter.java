package ru.wedwin.aggregator.adapter.out.output;

import ru.wedwin.aggregator.adapter.out.common.JacksonObjectMapper;
import ru.wedwin.aggregator.adapter.out.common.PayloadMapper;
import ru.wedwin.aggregator.domain.model.AggregatedRecord;
import ru.wedwin.aggregator.domain.model.OutputSpec;
import ru.wedwin.aggregator.port.out.OutputWriter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

public class JsonFileWriter implements OutputWriter {

    @Override
    public String id() {
        return "json";
    }

    @Override
    public void write(List<AggregatedRecord> records, OutputSpec spec) {
        ObjectMapper om = JacksonObjectMapper.instance();

        for (AggregatedRecord rec: records) {
//            Files.createDirectories(spec.path().getParent()); // todo
            ObjectNode obj = om.createObjectNode();
            obj.put("itemId", rec.itemId().toString());
            obj.put("apiId", rec.apiId().toString());
            obj.put("timestamp", rec.timestamp().toString());
            obj.set("payload", PayloadMapper.toJsonNode(rec.payload(), om));

            om.writerWithDefaultPrettyPrinter().writeValue(spec.path().toFile(), obj);
        }
    }
}
