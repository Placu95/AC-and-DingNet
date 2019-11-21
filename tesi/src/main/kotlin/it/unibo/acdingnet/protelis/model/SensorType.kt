package it.unibo.acdingnet.protelis.model

import it.unibo.acdingnet.protelis.model.sensorconverter.DefaultConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.SensorConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.UnssupportedConversion

enum class SensorType(val type: String, val lenght: Int, private val converter: SensorConverter = DefaultConverter()) {
    SOOT("soot", 1),
    CO2("CO2", 1),
    GPS("gps", 8, UnssupportedConversion());

    fun convertToDouble(data: MutableList<Byte>): Double = converter.convert(lenght, data)

}