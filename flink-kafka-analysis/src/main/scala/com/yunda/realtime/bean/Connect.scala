package com.yunda.realtime.bean

case class Connect(
                    depart_proof:String,   //发车凭证
                    depart_proof_type:Int,  //发车凭证类型
                    car_number:String,      //车牌号
                    car_type:Int,           //车辆类型
                    site_type:Int,          //站点类型
                    site_enconding:String,       //站点编码
                    gross_weight:Double,    // 毛重kg
                    average_tare:Double,    //平均皮重
                    state:Int,              //进出状态
                    weighing_time:Long,     //称重时间
                    warehouse_time:Long,     //入库时间
                    ship_id:Long, //运单号
                    scan_type:Long, //扫描类型
                    package_weight:Double, //包重量
                    count_number:Int  //票件数
                  )
