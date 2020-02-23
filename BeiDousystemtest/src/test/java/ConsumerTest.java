import com.alibaba.fastjson.JSONObject;
import com.yunda.bean.BeiDou;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerTest {
    public static void main(String[] args) {
        //1. 创建一个kafka的消费者
        Properties prop = new Properties();

        prop.put("bootstrap.servers", "master1.yunda:9092");
        prop.put("group.id", "hello");  // 这个消费者归宿那个组的
        prop.put("enable.auto.commit", "true");  // 自动提交
        prop.put("auto.commit.interval.ms", "1000"); //  自定提交的时间间隔:
        prop.put("auto.offset.reset","earliest");
        prop.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");  // 反序列化
        prop.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(prop);


        //2. 确定要接收那个topic的数据:  subscribe  订阅

        consumer.subscribe(Arrays.asList("HKT_HKGLSETL_HKCOSTCALC"));

        //3. 获取数据  redis
        //while (true) {

        ConsumerRecords<String, String> records = consumer.poll(10000);
        for (ConsumerRecord<String, String> record : records) {

            String value = record.value();
            System.out.println(value);
            BeiDou beiDou1 = JSONObject.parseObject(value, BeiDou.class);


            System.out.println(beiDou1.toString());

        }
        //}

        //4. 释放资源

        consumer.close();
    }
}
