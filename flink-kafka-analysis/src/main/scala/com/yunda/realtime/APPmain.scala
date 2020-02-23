package com.yunda.realtime

import java.io.FileInputStream
import java.util.Properties

import com.yunda.realtime.Utils.{LoadometerWM, ScanWM, TimeUtil}
import com.yunda.realtime.avro.ScanSourceYunDa
import com.yunda.realtime.bean._
import com.yunda.realtime.process._
import com.yunda.realtime.serializer.{BusiGrossWeightSchema, ProducerSchema, ScanDeserializationSchema}
import com.yundasys.domain.BusiGrossWeight
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, FlinkKafkaProducer011}
object APPmain {
    def main(args: Array[String]): Unit = {
        // TODO 1.获取flink运行环境及配置文件信息
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
        env.setParallelism(1)

        val config = new Properties
        //文件要放到resource文件夹下
        val path: String = getClass.getResource("/config.properties").getPath

        config.load(new FileInputStream(path))
        val groupId = config.getProperty("group.id")
        //地磅系统topic
        val KAFKA_TOPIC_LOADOMETER = config.getProperty("loadometer.topic")
        //扫描系统topic
        val KAFKA_TOPIC_SCAN = config.getProperty("scan.topic")
        //北斗系统topic
        val KAFAK_TOPIC_BeiDou =config.getProperty("beidou.topic")

        //TODO 2.地磅系统kafkaConsumer配置
        val KafkaConsumerProps = new Properties()
        KafkaConsumerProps.setProperty("bootstrap.servers", config.getProperty("bootstrap.servers"))
        KafkaConsumerProps.setProperty("group.id", groupId)
       val consumer: FlinkKafkaConsumer011[BusiGrossWeight] = new FlinkKafkaConsumer011[BusiGrossWeight](KAFKA_TOPIC_LOADOMETER,new BusiGrossWeightSchema(),KafkaConsumerProps)
      // consumer.setStartFromLatest()
         consumer.setStartFromEarliest()
       val loadometer: DataStream[Loadometer] = env.addSource(consumer).map(data=>{
       //将时间类型转换成毫秒时间戳
       val weightTime: Long = TimeUtil.getTimestamp(data.getWeightTime)
       val warehouseTime: Long = TimeUtil.getTimestamp(data.getCreateTime)
       Loadometer(data.getTransDocId,data.getTransDocIdType.toInt,data.getCarId,data.getCarType.toInt,data.getSiteType.toInt,
         data.getSiteCode,data.getGrossWeight.toDouble,data.getTareWeight.toDouble,data.getInOutFlag.toInt,weightTime,warehouseTime)
        })
       //设置eventTime字段及水印???
       // .assignTimestampsAndWatermarks(new LoadometerWM())
       //loadometer.print("loadometer")

       //TODO 3.对地磅称重系统数据流进行预处理(装载/卸载地磅净重)
       //TODO 3.1(非途径站站点)
       //TODO 3.1.1(非途径站站点)进站状态
       val filterData0: DataStream[Loadometer] = loadometer.filter(message => {
         message.state==0 && message.site_type!=1
       })
       //TODO 3.1.2(非途径站站点)出站状态
       val filterData1: DataStream[Loadometer] = loadometer.filter(message => {
         message.state==1 && message.site_type!=1
       })

       //TODO 3.2根据进出站状态计算装车,卸车地磅净重重量
       val connectData1: DataStream[(Loadometer, Loadometer)] = filterData0.keyBy("depart_proof", "site_encoding")
       .connect(filterData1.keyBy("depart_proof", "site_encoding"))
       .process(new LoadometerProcess)
        val connectData1Process: DataStream[LoadometerType0] = connectData1.map(data => {
         //todo 当为:网络+始发站时+高栏
        if (data._1.depart_proof_type==0 && data._1.site_type == 0 && data._1.car_type == 0) {
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._2.gross_weight - data._1.gross_weight, data._1.warehouse_time)
         //todo 当为:网络+终点站时+高栏
       } else if (data._1.depart_proof_type==0 && data._1.site_type == 2 && data._1.car_type == 0) {
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._1.gross_weight - data._2.gross_weight, data._1.warehouse_time)
         //todo 当为:网络+始发站+非高栏
       } else if (data._1.depart_proof_type==0 && data._1.site_type == 0 && data._1.car_type == 1) {
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._2.gross_weight - data._2.average_tare, data._1.warehouse_time)
         //todo 当为：网络+终点站+非高栏
       } else if(data._1.depart_proof_type==0 && data._1.site_type == 2 && data._1.car_type == 1){
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._1.gross_weight - data._1.average_tare, data._1.warehouse_time)
         //todo 当为：物流卡班/网点直跑车+始发站
       }else if((data._1.depart_proof_type==1 || data._1.depart_proof_type==2) && data._1.site_type == 0){
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._2.gross_weight - data._1.gross_weight, data._1.warehouse_time)
         //todo 当为:物流卡班/网点直跑车+终点站
       }else{
         LoadometerType0(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
           data._1.gross_weight, data._1.gross_weight - data._2.gross_weight, data._1.warehouse_time)
         }
       })
       connectData1Process.print("非途径站")

      //TODO 3.3(途径站站点)
         //TODO 3.3.1(途径站站点)进站状态
         val filterType01: DataStream[Loadometer] = loadometer.filter(message => {
           message.state == 0 && message.site_type == 1
         })
         //TODO 3.3.2(途径站站点)出站站状态
         val filterType11 = loadometer.filter(message => {
           message.state == 1 && message.site_type == 1
         })
        //filterType11.print("filterType11")
         //TODO 3.3.3(途径站站点)即装即卸
         val filterType21: DataStream[Loadometer] = loadometer.filter(message => {
           message.state==2 && message.site_type==1
         })
         // 获取进站、出站称重
         val connect1: DataStream[LoadometerType1] = filterType01.keyBy("depart_proof", "site_encoding")
                .connect(filterType11.keyBy("depart_proof", "site_encoding"))
                  .process(new LoadometerProcess)
                     .map(data => {
                  LoadometerType1(data._1.depart_proof, data._1.site_type, data._1.site_encoding,
                    data._1.gross_weight, data._2.gross_weight, data._1.warehouse_time)
                })
        //获取即装即卸重量
         val connect2: DataStream[LoadometerType21] = connect1.keyBy("depart_proof", "site_encoding")
            .connect(filterType21.keyBy("depart_proof", "site_encoding"))
             .process(new LoadometerConnectType21)
               .map(data => {
         LoadometerType21(data._1.depart_proof, data._1.site_type, data._1.site_encoding, data._1.loadometer_in_weight,
           data._1.loadometer_out_weight, data._2.gross_weight, data._1.database_timestamp)
       })


       //TODO 4.扫描系统kafkaConsumer
       val properties = new Properties()
       properties.setProperty("bootstrap.servers", config.getProperty("bootstrap.servers"))
       properties.setProperty("group.id", groupId)
       val scanConsumer: FlinkKafkaConsumer011[ScanSourceYunDa] = new FlinkKafkaConsumer011[ScanSourceYunDa](KAFKA_TOPIC_SCAN,new ScanDeserializationSchema,properties)
       //指定offset进行消费
       /*import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition
       val map: util.HashMap[KafkaTopicPartition, lang.Long] = new java.util.HashMap[KafkaTopicPartition,lang.Long]()
        map.put(new KafkaTopicPartition("scan_source_yunda", 0),1000L)
       scanConsumer.setStartFromSpecificOffsets(map)*/
       //scanConsumer.setStartFromLatest()
       scanConsumer.setStartFromEarliest()
       val scanSource: DataStream[ScanSourceYunDa] = env.addSource(scanConsumer)
       // scanSource.print("scanSource")

      //TODO 5.扫描系统数据预处理
      //TODO 5.1提取有用字段
      val scan: DataStream[Scan] = scanSource.map(data => {
      Scan(
      data.getShipId.longValue(),   //运单号
      data.getScanSite.intValue(),  //站点
      data.getScanTyp.longValue(),  //站点类型
      data.getScanTm.longValue(),   //时间戳
      data.getFrgtWgt.toDouble,     //包重量
      data.getInsDbTm.longValue(),  //入库时间
      data.getRmkInf.toString,      //发车凭证
      1 )                           //同一个面单为一个票件数
     })
        //设置时间水位线
     // .assignTimestampsAndWatermarks(new ScanWM())
     //测试数据能否正常接入
     // scan.print("scan")
     //TODO 5.2计算扫描重量及发车票件数(通过时间窗口进行的计算)
        /*
          装车扫描重量KG（凭证级）：该凭证在某站点所有装车面单扫描重量之和（如同一面单有多次扫描重量取最轻）
          卸车扫描重量KG（凭证级）：该凭证在某站点所有卸车面单扫描重量之和（如同一面单有多次扫描重量取最轻）
         */
        val resultScan: DataStream[ScanResult] =scan.keyBy(data => {data.rmk_inf + data.scan_site + data.ship_id})
                //TODO 窗口时间需要明确==>>> 自定义窗口 (如何实现在每个计算窗口内  同一车辆在某站点的装卸活动全部完成?)
                .timeWindow(Time.seconds(10))
                .min("package_weight")
                .filter(data => {96 == data.scan_type || 97 == data.scan_type}) //过滤扫描为装卸扫描类型
                .keyBy(data => {data.rmk_inf + data.scan_site}) //分流
                .process(new ScanKeyedProcess())
      //TODO 5.2计算扫描重量及发车票件数(通过触发事件进行的计算)
      //TODO 需要将loadometer替换为触发事件
     /* val resultScan: DataStream[ScanResult] = scan.keyBy(data => {data.rmk_inf + data.scan_site})
                                     .connect(loadometer.keyBy(data => {data.depart_proof + data.site_encoding}))
                                     .process(new TriggerNode())*/


      //TODO 6.对扫描系统数据和地磅系统数据连接起来，共同处理
      // TODO 6.1将地磅系统（非途径站）和扫描系统流整合
       val resultBeidou1: DataStream[WgtFromFlinkTmp] = connectData1Process.keyBy(data=>{data.depart_proof+data.site_encoding}) //分流
            .connect(resultScan.keyBy(data=>{data.rmk_inf+data.scan_site})) //连接两个流
            .process(new LoadometerConnectScan1) //转换成一个流
       .map(data => {
      //当地磅系统为始发站时,无卸车扫描
       if (data._1.site_type == 0) { //装车
       WgtFromFlinkTmp(
          data._1.depart_proof, data._1.site_type.toString, data._1.site_encoding.toString, data._1.loadometer_gross_weight.toString, data._1.loadometer_net_weight.toString,
        "0.0", "0.0", "0.0", data._2.load_weight.toString, data._2.load_count_number.toString, "0.0", "0", data._1.database_timestamp.toString)
      //当地磅系统为终点站，无装车扫描
       }else {  //卸车
      WgtFromFlinkTmp(
          data._1.depart_proof, data._1.site_type.toString, data._1.site_encoding.toString,"0.0", "0.0", data._1.loadometer_gross_weight.toString, data._1.loadometer_net_weight.toString,
        "0.0","0.0", "0", data._2.unload_weight.toString, data._2.unload_count_number.toString,  data._1.database_timestamp.toString)
         }
       })
      // TODO 6.2将地磅系统(途径站)与扫描系统流整合
       val resultBeidou2: DataStream[WgtFromFlinkTmp] = connect2.keyBy(data=>{data.depart_proof+data.site_encoding})
       .connect(resultScan.keyBy(data=>{data.rmk_inf+data.scan_site}))//连接扫描表
       .process(new LoadometerConnectScan2)

      //TODO 6.3将途径站和非途径站流汇总
      val result: DataStream[WgtFromFlinkTmp] = resultBeidou1.union(resultBeidou2)

      //TODO 6将计算结果下沉到kafka

       lazy val kafkaProducerProps = new Properties()
       kafkaProducerProps.setProperty("bootstrap.servers", config.getProperty("bootstrap.servers"))
       kafkaProducerProps.put("acks", "all")
       kafkaProducerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
       kafkaProducerProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

       val producer: FlinkKafkaProducer011[WgtFromFlinkTmp] = new FlinkKafkaProducer011[WgtFromFlinkTmp](KAFAK_TOPIC_BeiDou,new ProducerSchema(),kafkaProducerProps)
       result.addSink(producer)
       result.print()
       env.execute("BeiDouJob")

    }

}
