package com.yunda.realtime.bean

case class LoadometerType1(
                           //发车凭证
                           depart_proof:String,
                           //站点类型
                           site_type:Int,
                           //站点编码
                           site_encoding:String,
                           //进站地磅重量
                           loadometer_in_weight:Double,
                           //出站地磅重量
                           loadometer_out_weight:Double,
                           //入库时间
                           database_timestamp:Long)


