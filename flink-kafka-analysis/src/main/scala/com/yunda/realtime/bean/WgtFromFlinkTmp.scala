package com.yunda.realtime.bean

case class WgtFromFlinkTmp(
                  //发车凭证
                  depart_proof:String,
                  //站点类型
                  site_type:String,
                  //站点编码
                  site_encoding:String,
                  //装车地磅毛重
                  loadometer_load_gross_weight:String,
                  //装车地磅净重
                  loadometer_load_net_weight:String,
                  //卸车地磅毛重
                  loadometer_unload_gross_weight:String,
                  //卸车地磅净重
                  loadometer_unload_net_weight:String,
                  //即装即卸地磅重量
                  loadometer_load_unload_weight:String,
                  //装车扫描重量KG
                  scan_load_weight:String,
                  //装车票件数
                  load_number:String,
                  //卸车扫描重量KG
                  scan_unload_weight:String,
                  //卸车票件数
                  unload_number:String,
                  //入库时间
                  database_timestamp:String
                 )



