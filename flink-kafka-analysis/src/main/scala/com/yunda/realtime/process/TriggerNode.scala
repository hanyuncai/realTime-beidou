package com.yunda.realtime.process

import com.yunda.realtime.bean.{Loadometer, Scan, ScanResult}
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor, ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.util.Collector

import scala.collection.mutable.ListBuffer

class TriggerNode extends CoProcessFunction[Scan,Loadometer,ScanResult]{
  lazy val scanListState: ListState[Scan] = getRuntimeContext.getListState[Scan](new ListStateDescriptor[Scan]("scan-state",classOf[Scan]))
  lazy val scan96State: ValueState[Scan] = getRuntimeContext.getState[Scan](new ValueStateDescriptor[Scan]("scan96-state",classOf[Scan]))
  lazy val scan97State: ValueState[Scan] = getRuntimeContext.getState[Scan](new ValueStateDescriptor[Scan]("scan97-state",classOf[Scan]))
  override def processElement1(value: Scan, ctx: CoProcessFunction[Scan, Loadometer, ScanResult]#Context, out: Collector[ScanResult]): Unit = {
    scanListState.add(value)
    //首先判断状态时候为空值
    if(scan96State.value()==null && value.scan_type==96){
       scan96State.update(value)
    }
    if (scan97State.value()==null && value.scan_type==97){
       scan97State.update(value)
    }

  }

  override def processElement2(value: Loadometer, ctx: CoProcessFunction[Scan, Loadometer, ScanResult]#Context, out: Collector[ScanResult]): Unit = {
    val scan96=scan96State.value()
    val scan97=scan97State.value()
    val scans= new ListBuffer[Scan]
    import scala.collection.JavaConversions._
    for(scan<-scanListState.get()){
      scans+=scan
    }
    scanListState.clear()
    //TODO 获取同一面单最小值
    val scansResult= new ListBuffer[Scan]
    val groupData: Map[Long, ListBuffer[Scan]] = scans.groupBy(data=>{data.ship_id})
    groupData.foreach{
    case (x,y)=>
         val data: ListBuffer[Scan] = y.sortBy(_.package_weight)(Ordering.Double)
         scansResult.add(data(0))
    }
    // TODO 获取同一发车凭证+站点  扫描重量及面单数
    for(value<-scansResult){
      if(value.scan_type==96){
        scan96State.update(
            Scan(value.ship_id, value.scan_site, value.scan_type, value.scan_timestamp, value.package_weight + scan96.package_weight,
              value.database_timestamp, value.rmk_inf, value.count_number + scan96.count_number)
        )
      }else if(value.scan_type==97){
        scan97State.update(
          Scan(value.ship_id, value.scan_site, value.scan_type, value.scan_timestamp, value.package_weight + scan97.package_weight,
            value.database_timestamp, value.rmk_inf, value.count_number + scan97.count_number)
        )
      }
    }

    //TODO 根据状态值判断装卸类型
    if(scan96State!=null && scan97State!=null){   //即装即卸
      out.collect(
        ScanResult(
          scan96State.value().scan_site, scan96State.value().scan_type + scan97State.value().scan_type, scan96State.value().package_weight,
          scan96State.value().count_number, scan97State.value().package_weight, scan97State.value().count_number, scan96.rmk_inf
        )
      )
      scan97State.clear()
      scan96State.clear()
    }else if(scan96State!=null && scan97State==null){  //只装不卸
      out.collect(
        ScanResult(
          scan96State.value().scan_site, scan96State.value().scan_type, scan96State.value().package_weight,
          scan96State.value().count_number, 0.0, 0, scan96State.value().rmk_inf
        )
      )
      scan96State.clear()
    }else if(scan96State==null && scan97State!=null) { //只卸不装
      out.collect(
      ScanResult(
        scan97State.value().scan_site, scan97State.value().scan_type, 0.0, 0,
        scan97State.value().package_weight, scan97State.value().count_number, scan97State.value().rmk_inf
      )
      )
      scan97State.clear()
    }
  }
}
