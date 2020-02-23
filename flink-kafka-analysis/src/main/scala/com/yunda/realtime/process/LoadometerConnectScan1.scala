package com.yunda.realtime.process
import com.yunda.realtime.bean.{LoadometerType0, ScanResult}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.streaming.api.scala._

class LoadometerConnectScan1 extends CoProcessFunction[LoadometerType0, ScanResult, (LoadometerType0, ScanResult)]{
  // 定义状态来保存已经到达的地磅事件和扫描事件
  //定义侧输出流tag
   val unmatchedLoadometer: OutputTag[LoadometerType0] = new OutputTag[LoadometerType0]("unmatchedLoadometer")
   val unmatchedScan: OutputTag[ScanResult] = new OutputTag[ScanResult]("unmatchedScan")
  lazy val loadometerState: ValueState[LoadometerType0] = getRuntimeContext.getState(new ValueStateDescriptor[LoadometerType0]("loadometer-State", classOf[LoadometerType0]))
  lazy val scanState: ValueState[ScanResult] = getRuntimeContext.getState(new ValueStateDescriptor[ScanResult]("scan-State", classOf[ScanResult]))
  override def processElement1(value: LoadometerType0, ctx: CoProcessFunction[LoadometerType0, ScanResult, (LoadometerType0, ScanResult)]#Context, out: Collector[(LoadometerType0, ScanResult)]): Unit = {
    // 判断有没有地磅数据的事件
    val scan: ScanResult = scanState.value()
    if (scan != null) {
      // 如果已经有scan, 在主流输出匹配信息, 清空状态
      out.collect((value, scan))
      scanState.clear()
    } else {
      // 如果还没到，那么把地磅存入状态，并且注册一个定时器等待,等待30秒，30秒后告警
      loadometerState.update(value)
      ctx.timerService().registerEventTimeTimer(value.database_timestamp * 1000L + 15000L)
    }
  }

  override def processElement2(value: ScanResult, ctx: CoProcessFunction[LoadometerType0, ScanResult, (LoadometerType0, ScanResult)]#Context, out: Collector[(LoadometerType0, ScanResult)]): Unit = {
    // 同样的处理流程
    val loadometer: LoadometerType0 = loadometerState.value()
    if (loadometer != null) {
      out.collect((loadometer, value))
      loadometerState.clear()
    } else {
      // 如果还没到，那么把地磅存入状态，并且注册一个定时器等待,等待30秒，30秒后告警
      scanState.update(value)
      ctx.timerService().registerEventTimeTimer(ctx.timerService().currentProcessingTime()+5000L)
    }
  }

  override def onTimer(timestamp: Long, ctx: CoProcessFunction[LoadometerType0, ScanResult, (LoadometerType0, ScanResult)]#OnTimerContext, out: Collector[(LoadometerType0, ScanResult)]): Unit = {
    // 到时间了，还没有收到某个事件， 那么输出报警信息
    if (loadometerState.value() != null) {
      // scan 没来, 输出loadometer到侧输出流
      ctx.output(unmatchedLoadometer, loadometerState.value())
    }
    if (scanState.value() != null) {
      // loadometer没来, 输出scan到侧输出流
      ctx.output(unmatchedScan, scanState.value())
    }
    loadometerState.clear()
    scanState.clear()
  }

}
