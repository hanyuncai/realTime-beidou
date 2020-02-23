package com.yunda;

import com.alibaba.fastjson.JSON;
import com.yunda.bean.BeiDou;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class BeiDouMain {
    public static void main(String[] args) {
        //TODO 获取配置文件配置
        Properties config = new Properties();
        try {
            config.load(BeiDouMain.class.getClassLoader().getResourceAsStream("application.conf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO  获取kafka配置信息
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", config.getProperty("bootstrap.servers"));
        //设置生产者的序列化方式：要求以JSON对象格式传输对象  需要自定义序列化方式
        properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //   properties.setProperty("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.setProperty("partitioner.class","com.yunda.utils.UDFPartitioner");
        KafkaProducer<String, String> producer = new KafkaProducer<String,String>(properties);
        //    KafkaProducer<String, Object> producer = new KafkaProducer<String,Object>(properties);
        System.out.println(config.getProperty("topic"));
        Random random = new Random();
       try{
            while(true) {
                double weight1=random.nextDouble()*1000;
                double weight2=random.nextDouble()*1000;
                BeiDou beiDou = new BeiDou(

                        RandomStringUtils.randomAlphanumeric(10),
                        random.nextInt(3),
                        RandomStringUtils.randomAlphanumeric(6),
                        weight1,
                        weight1-random.nextInt(500),
                        random.nextInt(3),
                        weight2,
                        weight2-random.nextInt(500),
                        random.nextDouble()*1000,
                        random.nextInt(200) + 1,
                        random.nextDouble()*1000,
                        random.nextInt(200),
                        System.currentTimeMillis() - random.nextInt(1000000));
                //将对象转换成Json字符串
                String JsonString = JSON.toJSONString(beiDou);
                System.out.println(JsonString);

                //发送模拟数据到kafka
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(config.getProperty("topic"), JsonString);
                producer.send(record);
                producer.flush();
                // 控制数据发送频次
                Thread.sleep(500);
                System.out.println("数据发送成功");
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
