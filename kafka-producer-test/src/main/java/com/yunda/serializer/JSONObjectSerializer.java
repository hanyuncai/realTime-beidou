package com.yunda.serializer;
import org.apache.kafka.common.serialization.Serializer;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JSONObjectSerializer implements Serializer<Object> {
    private ObjectMapper objectMapper;
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper = new ObjectMapper();
    }

    public byte[] serialize(String topic, Object data) {
        byte[] ret = null;
        try {
            ret = objectMapper.writeValueAsString(data).getBytes("utf-8");
        } catch (IOException e) {
            System.out.println("序列化失败");
            e.printStackTrace();
        }
        return ret;
    }

    public void close() {

    }
}
