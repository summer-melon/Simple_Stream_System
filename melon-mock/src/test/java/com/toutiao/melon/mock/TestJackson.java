package com.toutiao.melon.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.toutiao.melon.mock.entiry.Event;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJackson {

    private static final Logger log = LoggerFactory.getLogger(TestJackson.class);

    @Test
    public void testEvent2Json() {
        ObjectMapper objectMapper = new ObjectMapper();
        Event event = new Event("apple", 5);
        try {
            String jsonStr = objectMapper.writeValueAsString(event);
            log.info("Json : {}", jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJson2Event() {
        ObjectMapper objectMapper = new ObjectMapper();
        String eventJson =
                "{\"word\":\"apple\",\"count\":5}";
        try {
            Event event = objectMapper.readValue(eventJson, Event.class);
            log.info("Parse: {}", event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tt() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ObjectNode childNode1 = mapper.createObjectNode();
        childNode1.put("name1", "val1");
        childNode1.put("name2", "val2");

        rootNode.set("obj1", childNode1);

        ObjectNode childNode2 = mapper.createObjectNode();
        childNode2.put("name3", "val3");
        childNode2.put("name4", "val4");

        rootNode.set("obj2", childNode2);

        ObjectNode childNode3 = mapper.createObjectNode();
        childNode3.put("name5", "val5");
        childNode3.put("name6", "val6");

        rootNode.set("obj3", childNode3);

        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(jsonString);
    }
}
