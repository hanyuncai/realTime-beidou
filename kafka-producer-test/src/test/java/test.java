import com.yunda.bean.Loadometer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.util.Arrays;
import java.util.Properties;

public class test {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "master1.yunda:9092,slave1.yunda:9092,slave2.yunda:9092");
        properties.setProperty("group.id","test04");
        properties.setProperty("enable.auto.commit","true");
        properties.setProperty("auto.commit.interval.ms","5000");
        properties.setProperty("auto.offset.reset","earliest");
        properties.setProperty("acks", "1");
//        properties.setProperty("acks", config.getProperty("acks"));
        //设置生产者的序列化方式：要求以JSON对象格式传输对象  需要自定义序列化方式
        properties.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer","com.yunda.serializer.JSONObjectDeserializer");
        // properties.setProperty("value.deserializer","org.apache.kafka.common.serialization.ByteArrayDeserializer");

        KafkaConsumer<String, Loadometer> consumer = new KafkaConsumer<String, Loadometer>(properties);
        consumer.subscribe(Arrays.asList("Loadometer"));
        while(true){
            //poll频率
            ConsumerRecords<String, Loadometer> consumerRecords = consumer.poll(100);
            for(ConsumerRecord<String, Loadometer> consumerRecord : consumerRecords){
                Loadometer value = consumerRecord.value();
                System.out.println(value.toString());
            }

        }
    }
}
