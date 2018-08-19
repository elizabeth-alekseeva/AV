package com.sksamuel.avro4s.record.encoder

import com.sksamuel.avro4s.internal.{Encoder, InternalRecord, SchemaEncoder}
import org.apache.avro.{Conversions, Schema}
import org.scalatest.{FlatSpec, Matchers}

case class WithBigDecimal(decimal: BigDecimal)

class DecimalEncoderTest extends FlatSpec with Matchers {

  import scala.collection.JavaConverters._

  "Encoder" should "use byte array for decimal" in {
    val schema = SchemaEncoder[WithBigDecimal].encode()

    val obj = WithBigDecimal(12.34)
    val s = schema.getField("decimal").schema()
    val bytes = new Conversions.DecimalConversion().toBytes(BigDecimal(12.34).bigDecimal, s, s.getLogicalType)

    Encoder[WithBigDecimal].encode(obj, schema) shouldBe InternalRecord(schema, Vector(bytes))
  }

  it should "support optional big decimals" in {

    case class Test(big: Option[BigDecimal])
    val schema = SchemaEncoder[Test].encode()

    val s = schema.getField("big").schema().getTypes.asScala.find(_.getType != Schema.Type.NULL).get
    val bytes = new Conversions.DecimalConversion().toBytes(BigDecimal(123.4).bigDecimal.setScale(2), s, s.getLogicalType)

    Encoder[Test].encode(Test(Some(123.4)), schema) shouldBe InternalRecord(schema, Vector(bytes))
    Encoder[Test].encode(Test(None), schema) shouldBe InternalRecord(schema, Vector(null))
  }
}
