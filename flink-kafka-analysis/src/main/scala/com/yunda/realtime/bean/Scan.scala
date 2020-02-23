package com.yunda.realtime.bean

case class Scan(
                 ship_id:Long, //运单号
                 scan_site:Int, //扫描站点
                 scan_type:Long, //扫描类型
                 scan_timestamp:Long,   //时间戳
                 package_weight:Double, //包重量
                 database_timestamp:Long, //入库时间
                 rmk_inf:String,   //发车凭证
                 count_number:Int  //票件数
 )




