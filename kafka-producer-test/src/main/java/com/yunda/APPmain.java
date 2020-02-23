package com.yunda;

import com.alibaba.fastjson.JSON;
import com.yunda.bean.Loadometer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
public class APPmain {

    public static void main(String[] args) throws InterruptedException {
        //TODO 获取配置文件配置
        Properties config = new Properties();
        try {
            config.load(APPmain.class.getClassLoader().getResourceAsStream("application.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO  获取kafka配置信息
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", "master1.yunda:9092,slave1.yunda:9092,slave2.yunda:9092");
        //设置生产者的序列化方式：要求以JSON对象格式传输对象  需要自定义序列化方式
        properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","com.yunda.serializer.JSONObjectSerializer");
     //   properties.setProperty("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.setProperty("partitioner.class","com.yunda.utils.UDFPartitioner");
        KafkaProducer<String, Object> producer = new KafkaProducer<String,Object>(properties);
    //    KafkaProducer<String, Object> producer = new KafkaProducer<String,Object>(properties);
        Random random = new Random();
        try {
            while(true) {
                Loadometer loadometer = new Loadometer(
                        RandomStringUtils.randomAlphanumeric(10),
                        random.nextInt(3),
                        RandomStringUtils.randomAlphanumeric(7),
                        random.nextInt(2),
                        random.nextInt(3),
                        RandomStringUtils.randomAlphanumeric(4),
                        random.nextDouble()*5000,
                        random.nextDouble()*5000,
                        random.nextInt(2) + 1,
                        System.currentTimeMillis(),
                        System.currentTimeMillis() - random.nextInt(1000000));
                Object obj = JSON.toJSON(loadometer);
               // ProducerRecord<String, Object> record = new ProducerRecord<String, Object>(config.getProperty("topic"), obj);
               // ProducerRecord<String, Object> record = new ProducerRecord<String, Object>(config.getProperty("topic"), obj);
                ProducerRecord<String, Object> record = new ProducerRecord<String, Object>("Loadometer", obj);
                producer.send(record);
                producer.flush();
                System.out.println(obj.toString());
                // 控制数据发送频次
                Thread.sleep(1000);
                String topic = config.getProperty("topic");
                System.out.println(topic);
              }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            producer.close();
        } finally {
            System.out.println("KafkaProducer cannot connect!!!");
        }
    }
}

