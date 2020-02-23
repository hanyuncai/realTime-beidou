package com.yunda.realtime.bean


case class LoadometerType0(//发车凭证
                           depart_proof:String,
                           //站点类型
                           site_type:Int,
                           //站点编码
                           site_encoding:String,
                           //地磅毛重
                           loadometer_gross_weight:Double,
                           //地磅净重
                           loadometer_net_weight:Double,
                           //入库时间
                           database_timestamp:Long)
