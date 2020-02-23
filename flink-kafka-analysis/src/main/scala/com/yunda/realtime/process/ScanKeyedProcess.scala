package com.yunda.realtime.process

import com.yunda.realtime.bean.{Scan, ScanResult}
import org.apache.flink.api.common.state.{ ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

class ScanKeyedProcess  extends KeyedProcessFunction[String,Scan,ScanResult]{
  lazy val scanState96: ValueState[Scan] = getRuntimeContext.getState(new ValueStateDescriptor[Scan]("scan96", classOf[Scan]))
  lazy val scanState97: ValueState[Scan] = getRuntimeContext.getState(new ValueStateDescriptor[Scan]("scan97", classOf[Scan]))

  override def processElement(value: Scan, ctx: KeyedProcessFunction[String, Scan, ScanResult]#Context, out: Collector[ScanResult]): Unit = {
    //首先判断状态时候为空值
    if(scanState96.value()==null && value.scan_type==96){
      scanState96.update(value)
    }
    if (scanState97.value()==null && value.scan_type==97){
      scanState97.update(value)
    }
    val scan1 = scanState96.value()
    val scan2 = scanState97.value()
    if (value.scan_type == 96) {
      scanState96.update(   //为装车时
        Scan(value.ship_id, value.scan_site, value.scan_type, value.scan_timestamp, value.package_weight + scan1.package_weight,
          value.database_timestamp, value.rmk_inf, value.count_number + scan1.count_number))
      ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 1000L)
    } else {  //为卸车时
      scanState97.update(
        Scan(value.ship_id, value.scan_site, value.scan_type, value.scan_timestamp, value.package_weight + scan2.package_weight, value.database_timestamp, value.rmk_inf, value.count_number + scan2.count_number))}
    ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 1000L)
  }
  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, Scan, ScanResult]#OnTimerContext, out: Collector[ScanResult]): Unit = {
    val scan96 = scanState96.value()
    val scan97 = scanState97.value()
    if (scan96 != null && scan97 != null) {
      out.collect(
        ScanResult(scan96.scan_site, scan96.scan_type + scan97.scan_type, scan96.package_weight, scan96.count_number, scan97.package_weight, scan97.count_number, scan96.rmk_inf))
      scanState96.clear()
      scanState97.clear()
    } else if (scan96 != null && scan97 == null) {
      out.collect(
        ScanResult(scan96.scan_site, scan96.scan_type, scan96.package_weight, scan96.count_number, 0.0, 0, scan96.rmk_inf))
    } else if (scan96 == null && scan97 != null) {
      out.collect(
        ScanResult(scan97.scan_site, scan97.scan_type, 0.0, 0, scan97.package_weight, scan97.count_number, scan97.rmk_inf))
    } else {
      return
    }
  }
}
