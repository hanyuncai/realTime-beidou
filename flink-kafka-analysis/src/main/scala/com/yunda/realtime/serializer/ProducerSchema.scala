package com.yunda.realtime.serializer

import java.io.ByteArrayOutputStream

import com.yunda.realtime.bean.WgtFromFlinkTmp
import org.apache.flink.api.common.serialization.SerializationSchema
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.avro.specific.SpecificDatumWriter

class ProducerSchema extends SerializationSchema[WgtFromFlinkTmp]{
  override def serialize(element: WgtFromFlinkTmp): Array[Byte] = {
    var outputStream: ByteArrayOutputStream = null
    try {
      outputStream = new ByteArrayOutputStream()
      val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null)
      val writer: SpecificDatumWriter[WgtFromFlinkTmp] = new SpecificDatumWriter[WgtFromFlinkTmp](classOf[WgtFromFlinkTmp])
      writer.write(element, encoder)
      encoder.flush()
      outputStream.toByteArray
    } catch {
      case exception: Exception => throw exception
    } finally if(outputStream != null){
      outputStream.close()
    }
  }
}
