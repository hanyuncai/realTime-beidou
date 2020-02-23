package com.yunda.realtime.Utils

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

object TimeUtil {

  def formatTime2day(timestamp:Long): String ={
    val fastDateFormat: FastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
    val str: String = fastDateFormat.format(timestamp)
    str
  }

 def oderDtimeFormat(Dtime:String):Long={
   val fastDateFormat: FastDateFormat= FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
   val str:Long=fastDateFormat.format(Dtime).toLong
   str
 }

  def getTimestamp(x:String) :Long={
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date: Date = format.parse(x)
    val time: Long = date.getTime()
    time
  }


}
