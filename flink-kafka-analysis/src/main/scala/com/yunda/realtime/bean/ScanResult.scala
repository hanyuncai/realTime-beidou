package com.yunda.realtime.bean

case class ScanResult(
                       scan_site:Int, //扫描站点
                       scan_type:Long, //扫描类型
                       load_weight:Double, //装车扫描重量
                       load_count_number:Int, //装车票件数
                       unload_weight:Double, //卸车扫描重量
                       unload_count_number:Int, //卸车票件数
                       rmk_inf:String //发车凭证
                     )


