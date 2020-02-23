package com.yunda.realtime.process

import com.yunda.realtime.bean.{Loadometer, LoadometerType1}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.util.Collector

class LoadometerConnectType21 extends CoProcessFunction[LoadometerType1,Loadometer,(LoadometerType1,Loadometer)]{
  // 定义状态来保存已经到达的地磅事件和扫描事件
  lazy val loadometerType1State: ValueState[LoadometerType1] = getRuntimeContext.getState(new ValueStateDescriptor[LoadometerType1]("loadometerType1-State", classOf[LoadometerType1]))
  lazy val loadometerState: ValueState[Loadometer] = getRuntimeContext.getState(new ValueStateDescriptor[Loadometer]("loadometer-State", classOf[Loadometer]))
  override def processElement1(value: LoadometerType1, ctx: CoProcessFunction[LoadometerType1, Loadometer, (LoadometerType1, Loadometer)]#Context, out: Collector[(LoadometerType1, Loadometer)]): Unit = {
    // 判断有没有地磅数据的事件
    val loadometer: Loadometer = loadometerState.value()
    if (loadometer != null) {
      // 如果已经有loadometer, 在主流输出匹配信息, 清空状态
      out.collect((value, loadometer))
      loadometerState.clear()
    } else {
      // 如果还没到，那么把loadometerState存入状态，并且注册一个定时器等待,等待30秒，30秒后告警
      loadometerType1State.update(value)
      ctx.timerService().registerEventTimeTimer(value.database_timestamp * 1000L + 15000L)
    }
  }

  override def processElement2(value: Loadometer, ctx: CoProcessFunction[LoadometerType1, Loadometer, (LoadometerType1, Loadometer)]#Context, out: Collector[(LoadometerType1, Loadometer)]): Unit = {
    // 同样的处理流程
    val loadometerType1: LoadometerType1 = loadometerType1State.value()
    if (LoadometerType1 != null) {
      out.collect((loadometerType1, value))
      loadometerType1State.clear()
    } else {
      loadometerState.update(value)
      // 如果还没到，那么把pay存入状态，并且注册一个定时器等待,等待30秒，30秒后告警
      ctx.timerService().registerEventTimeTimer(value.warehouse_time * 1000L + 15000L)
    }

  }

}
