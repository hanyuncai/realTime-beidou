package com.yunda.serializer;
import com.alibaba.fastjson.JSON;
import com.yunda.bean.Loadometer;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JSONObjectDeserializer implements Deserializer<Object> {
    private String encoding = "UTF8";

    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public Object deserialize(String topic, byte[] data) {
        /*Object object = null;
        try {
            // 创建ByteArrayInputStream对象
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            // 创建ObjectInputStream对象
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            // 从objectInputStream流中读取一个对象
            object = objectInputStream.readObject();
            // 关闭输入流
            byteArrayInputStream.close();
            // 关闭输入流
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 返回对象
        return object;*/
        Object object = JSON.parseObject(data, Loadometer.class);
        Loadometer loadometer =(Loadometer) object;
        return loadometer;
    }

    public void close() {

    }
}
