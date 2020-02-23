package com.yunda.realtime.process

import com.yunda.realtime.bean.Loadometer
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.util.Collector

class LoadometerProcess extends  CoProcessFunction[Loadometer, Loadometer, (Loadometer, Loadometer)]{
  // 定义状态保存事件
  // 定义状态来保存已经到达的进站事件和出站事件
  lazy val loadometertype0: ValueState[Loadometer] = getRuntimeContext.getState(new ValueStateDescriptor[Loadometer]("loadometer-state0", classOf[Loadometer]))
  lazy val loadometertype1: ValueState[Loadometer] = getRuntimeContext.getState(new ValueStateDescriptor[Loadometer]("loadometer-state1", classOf[Loadometer]))

  //进站数据处理
  override def processElement1(value: Loadometer, ctx: CoProcessFunction[Loadometer, Loadometer, (Loadometer, Loadometer)]#Context, out: Collector[(Loadometer, Loadometer)]): Unit = {
    val state1: Loadometer = loadometertype1.value()
    if (state1 != null) {
      // 如果已经有出站数据, 在主流输出匹配信息, 清空状态
      out.collect((value, state1))
      loadometertype1.clear()
    } else {
      // 如果还没到，那么把进站数据存入状态，并且注册一个定时器等待,
      //需要和产品沟通进出站状态时间多久能到
      loadometertype0.update(value)
       ctx.timerService().registerEventTimeTimer(value.weighing_time * 1000L + 5000L)
    }
  }
  //出站数据处理
  override def processElement2(value: Loadometer, ctx: CoProcessFunction[Loadometer, Loadometer, (Loadometer, Loadometer)]#Context, out: Collector[(Loadometer, Loadometer)]): Unit = {
    val state0 = loadometertype0.value()
    if (state0 != null) {
      out.collect(state0, value)
      loadometertype0.clear()
    } else {
      loadometertype1.update(value)
      ctx.timerService().registerEventTimeTimer(value.weighing_time*1000L+5000L)
    }
  }
  //设置时间回调函数
  override def onTimer(timestamp: Long, ctx: CoProcessFunction[Loadometer, Loadometer, (Loadometer, Loadometer)]#OnTimerContext, out: Collector[(Loadometer, Loadometer)]): Unit = {
    //当时间到达后
  }
}
