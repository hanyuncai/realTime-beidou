package com.yunda.utils;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UDFPartitioner implements Partitioner {

    //计数器
    //每一次发送数据到kafka中的时候，都会加一次数+1
    //让计数器模拟分区的数据， 线程安全的整形类型
    private AtomicInteger counter = new AtomicInteger(0);
    @Override
    public int partition(String topic, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        //获取topic的partition信息
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        //获取分区数量
        int partitionNum=partitionInfos.size();
        //获取要发送的partition编号
        int partitionid =  counter.incrementAndGet() % partitionNum;
        if(counter.get() > 65545)
            counter.set(0);
        return partitionid;
    }
    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
