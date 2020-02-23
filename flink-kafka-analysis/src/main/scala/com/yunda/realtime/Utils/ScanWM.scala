package com.yunda.realtime.Utils

import com.yunda.realtime.bean.Scan
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class ScanWM extends AssignerWithPeriodicWatermarks[Scan]{
  //设置延迟时间 1min(需要和产品确认)
  val bound:Long= 1000*60
  //观察到的最大时间戳
  var maxTs:Long=Long.MinValue
  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTs-bound)
  }

  override def extractTimestamp(element: Scan, previousElementTimestamp: Long): Long = {
    maxTs=maxTs.max(element.scan_timestamp)
    element.scan_timestamp
  }
}
