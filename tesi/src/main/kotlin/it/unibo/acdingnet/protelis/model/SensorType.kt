package it.unibo.acdingnet.protelis.model

import it.unibo.acdingnet.protelis.model.sensorconverter.DefaultConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.SensorConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.UnsupportedConversion

enum class SensorType(val lenght: Int = 1, private val converter: SensorConverter = DefaultConverter()) {
    GPS(8, UnsupportedConversion()),
    PM10,
    NO2(2),
    IAQ(0, UnsupportedConversion());

    fun convertToDouble(data: MutableList<Byte>): Double = converter.convert(lenght, data)

}