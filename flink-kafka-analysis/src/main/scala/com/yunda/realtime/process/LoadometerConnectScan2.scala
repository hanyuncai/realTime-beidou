package com.yunda.realtime.process

import com.yunda.realtime.bean.{LoadometerType21, ScanResult, WgtFromFlinkTmp}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
class LoadometerConnectScan2 extends CoProcessFunction[LoadometerType21,ScanResult,WgtFromFlinkTmp]{
  //判断装卸类型：
  /*
  *①只装不卸：途径站有装车扫描无卸车扫描；
  *②只卸不装：途径站无装车扫描有卸车扫描；
  *③有装有卸：途径站有装车扫描有卸车扫描；
  *④不装不卸：途径站无装车扫描无卸车扫描；
  *⑤轻装轻卸：只卸不装中的卸货重量小于100KG，只装不卸中的装货重量小于100KG，有装有卸中的装货重量和卸货重量均小于100KG；
  * */
  //设置测输出流
  lazy val scanTag: OutputTag[ScanResult] = new OutputTag[ScanResult]("unmatchScan")
  lazy val loadometerTag = new OutputTag[LoadometerType21]("unmatchLoadometer")
  lazy val scanState: ValueState[ScanResult] = getRuntimeContext.getState[ScanResult](new ValueStateDescriptor[ScanResult]("scan-state",classOf[ScanResult]))
  lazy val loadometerState: ValueState[LoadometerType21] = getRuntimeContext.getState(new ValueStateDescriptor[LoadometerType21]("loadometer-state", classOf[LoadometerType21]))
  override def processElement1(loadometer: LoadometerType21, ctx: CoProcessFunction[LoadometerType21, ScanResult, WgtFromFlinkTmp]#Context, out: Collector[WgtFromFlinkTmp]): Unit = {
    val scan = scanState.value()
    if(scan==null){
      loadometerState.update(loadometer)
      //TODO 设置定时器(明确scan流多久能到达)
      ctx.timerService().registerEventTimeTimer(ctx.timerService().currentProcessingTime()+10000L)
      }else{
      //TODO 当为即装即卸类型时
     if(scan.scan_type==193 && (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
      out.collect(
        WgtFromFlinkTmp(
        loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,(loadometer.loadometer_in_weight-loadometer.loadometer_load_unload_weight).toString,
        loadometer.loadometer_out_weight.toString,(loadometer.loadometer_out_weight-loadometer.loadometer_load_unload_weight).toString,loadometer.loadometer_load_unload_weight.toString,
        scan.load_weight.toString, scan.load_count_number.toString,scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
      scanState.clear()
      //TODO 当为只卸不装
     }else if(scan.scan_type==97 && (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
      out.collect(
        WgtFromFlinkTmp(
        loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,(loadometer.loadometer_in_weight-loadometer.loadometer_load_unload_weight).toString,
        loadometer.loadometer_out_weight.toString,"0",loadometer.loadometer_load_unload_weight.toString,
        scan.load_weight.toString,scan.load_count_number.toString,"0","0",loadometer.database_timestamp.toString))
      scanState.clear()
      //TODO 当为只卸不装
     }else if(scan.scan_type==96 &&  (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
      out.collect(
        WgtFromFlinkTmp(
        loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
        loadometer.loadometer_out_weight.toString,(loadometer.loadometer_out_weight-loadometer.loadometer_in_weight).toString,loadometer.loadometer_load_unload_weight.toString,
        "0","0",scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
      scanState.clear()
     }//TODO 当为轻装轻卸
    else if((loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs<=100){
      out.collect(
        WgtFromFlinkTmp(
        loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
        loadometer.loadometer_out_weight.toString,"0",loadometer.loadometer_load_unload_weight.toString,
        scan.load_weight.toString,scan.load_count_number.toString,scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
      scanState.clear()
    }else{ //TODO 当为不装不卸
      out.collect(
        WgtFromFlinkTmp(
        loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
        loadometer.loadometer_out_weight.toString,"0","0",
        "0","0","0","0",loadometer.database_timestamp.toString))
        }
      scanState.clear()
    }

  }

     //扫描重量事件处理
   override def processElement2(scan: ScanResult, ctx: CoProcessFunction[LoadometerType21, ScanResult, WgtFromFlinkTmp]#Context, out: Collector[WgtFromFlinkTmp]): Unit = {
     val loadometer: LoadometerType21 = loadometerState.value()
     if(loadometer==null){
       scanState.update(scan)
       ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime()+10000L)
     }else{
       if(scan.scan_type==193 && (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
         out.collect(
           WgtFromFlinkTmp(
             loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,(loadometer.loadometer_in_weight-loadometer.loadometer_load_unload_weight).toString,
             loadometer.loadometer_out_weight.toString,(loadometer.loadometer_out_weight-loadometer.loadometer_load_unload_weight).toString,loadometer.loadometer_load_unload_weight.toString,
             scan.load_weight.toString, scan.load_count_number.toString,scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
         scanState.clear()
         //TODO 当为只卸不装
       }else if(scan.scan_type==97 && (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
         out.collect(
           WgtFromFlinkTmp(
           loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,(loadometer.loadometer_in_weight-loadometer.loadometer_load_unload_weight).toString,
           loadometer.loadometer_out_weight.toString,"0",loadometer.loadometer_load_unload_weight.toString,
           scan.load_weight.toString,scan.load_count_number.toString,"0","0",loadometer.database_timestamp.toString))
         scanState.clear()
         //TODO 当为只卸不装
       }else if(scan.scan_type==96 &&  (loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs>100){
         out.collect(
           WgtFromFlinkTmp(
           loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
           loadometer.loadometer_out_weight.toString,(loadometer.loadometer_out_weight-loadometer.loadometer_in_weight).toString,loadometer.loadometer_load_unload_weight.toString,
           "0","0",scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
         scanState.clear()
       }//TODO 当为轻装轻卸
       else if((loadometer.loadometer_in_weight-loadometer.loadometer_out_weight).abs<=100){
         out.collect(
           WgtFromFlinkTmp(
             loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
             loadometer.loadometer_out_weight.toString,"0",loadometer.loadometer_load_unload_weight.toString,
             scan.load_weight.toString,scan.load_count_number.toString,scan.unload_weight.toString,scan.unload_count_number.toString,loadometer.database_timestamp.toString))
         scanState.clear()
       }else{ //TODO 当为不装不卸
         out.collect(
           WgtFromFlinkTmp(
             loadometer.depart_proof,loadometer.site_type.toString,loadometer.site_encoding,loadometer.loadometer_in_weight.toString,"0",
             loadometer.loadometer_out_weight.toString,"0","0",
             "0","0","0","0",loadometer.database_timestamp.toString))
         scanState.clear()
       }

     }

  }

   override def onTimer(timestamp: Long, ctx: CoProcessFunction[LoadometerType21, ScanResult, WgtFromFlinkTmp]#OnTimerContext, out: Collector[WgtFromFlinkTmp]): Unit = {
     //到定时器，还没有收到某个事件， 那么输出报警信息(测输出流)
     if(scanState.value()!=null){
       ctx.output(scanTag,scanState.value())
     }
     if(loadometerState.value()!=null){
       ctx.output(loadometerTag,loadometerState.value())
     }

     scanState.clear()
     loadometerState.clear()
  }
}
