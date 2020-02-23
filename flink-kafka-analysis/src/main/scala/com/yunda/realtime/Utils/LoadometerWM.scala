package com.yunda.realtime.Utils

import com.yunda.realtime.bean.Loadometer
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class LoadometerWM extends AssignerWithPeriodicWatermarks[Loadometer]{

  //设置延迟时间 1min(需要和产品确认)
  val bound:Long= 1000*60
  //观察到的最大时间戳
  var maxTs:Long=Long.MinValue
  override def getCurrentWatermark: Watermark = {
    new Watermark(maxTs-bound)
  }

  override def extractTimestamp(element: Loadometer, previousElementTimestamp: Long): Long = {
     maxTs=maxTs.max(element.warehouse_time)
     element.warehouse_time
  }
}
