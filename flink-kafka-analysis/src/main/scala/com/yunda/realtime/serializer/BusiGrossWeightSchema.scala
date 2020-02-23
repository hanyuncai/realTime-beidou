package com.yunda.realtime.serializer
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import com.yundasys.domain.BusiGrossWeight

class BusiGrossWeightSchema extends DeserializationSchema[BusiGrossWeight]{

    override def isEndOfStream(nextElement: BusiGrossWeight): Boolean ={
        false
    }

    override def deserialize(message: Array[Byte]): BusiGrossWeight = {
        var busiGross:BusiGrossWeight=null
        val bais = new ByteArrayInputStream(message)
        val ois = new ObjectInputStream(bais)
        busiGross = ois.readObject.asInstanceOf[BusiGrossWeight]
        busiGross
    }

    override def getProducedType: TypeInformation[BusiGrossWeight] = {
        TypeInformation.of(classOf[BusiGrossWeight])
    }
}
