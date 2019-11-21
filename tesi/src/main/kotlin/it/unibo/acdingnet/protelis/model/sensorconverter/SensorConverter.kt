package it.unibo.acdingnet.protelis.model.sensorconverter

import java.nio.ByteBuffer

interface SensorConverter {
    fun convert(length: Int, data: MutableList<Byte>): Double
}

class DefaultConverter: SensorConverter {
    override fun convert(length: Int, data: MutableList<Byte>): Double {
        var count = 0
        val pair = data.partition { count++ <length }
        data.removeAll(pair.first)
        val buffer = ByteBuffer.wrap(pair.first.toByteArray())
        return when(length) {
            Byte.SIZE_BYTES -> buffer.get().toDouble()
            Short.SIZE_BYTES -> buffer.short.toDouble()
            4 -> buffer.float.toDouble()
            8 -> buffer.double
            else -> throw IllegalStateException("impossible convert. Sensor with unknown length")
        }

    }
}

class UnssupportedConversion: SensorConverter {
    override fun convert(length: Int, data: MutableList<Byte>): Double = throw UnsupportedOperationException()
}