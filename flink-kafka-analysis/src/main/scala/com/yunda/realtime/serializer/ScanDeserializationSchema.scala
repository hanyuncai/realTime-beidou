package com.yunda.realtime.serializer


import java.io.ByteArrayInputStream
import com.yunda.realtime.avro.ScanSourceYunDa
import org.apache.avro.io.{BinaryDecoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation

class ScanDeserializationSchema extends DeserializationSchema[ScanSourceYunDa]{
    override def deserialize(message: Array[Byte]): ScanSourceYunDa = {
        val inputStream: ByteArrayInputStream = new ByteArrayInputStream(message)
        val reader: SpecificDatumReader[ScanSourceYunDa] = new SpecificDatumReader(ScanSourceYunDa.getClassSchema)
        val decoder: BinaryDecoder = DecoderFactory.get().binaryDecoder(inputStream, null)
        val scan: ScanSourceYunDa = reader.read(new ScanSourceYunDa(), decoder)
        scan
    }

    override def isEndOfStream(nextElement: ScanSourceYunDa): Boolean = {
        false
    }

    override def getProducedType: TypeInformation[ScanSourceYunDa] = {
        TypeInformation.of(classOf[ScanSourceYunDa])
    }
}
