package it.unibo.acdingnet.protelis.model

import it.unibo.acdingnet.protelis.model.sensorconverter.DefaultConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.SensorConverter
import it.unibo.acdingnet.protelis.model.sensorconverter.UnsupportedConversion

enum class SensorType(val lenght: Int = 1, val levels: List<Pair<Int, Int>> = emptyList(), private val converter: SensorConverter = DefaultConverter()) {
    GPS(8, converter = UnsupportedConversion()),
    PM10(levels = listOf(Pair(0, 25), Pair(25, 50), Pair(50, 90), Pair(90, 180), Pair(180, 255))),
    NO2(2, listOf(Pair(0, 50), Pair(50, 100), Pair(100, 200), Pair(200, 400), Pair(400, 600))),
    IAQ(0, listOf(Pair(0, 25), Pair(25, 50), Pair(50, 75), Pair(75, 100), Pair(100, 125)), UnsupportedConversion());

    fun convertToDouble(data: MutableList<Byte>): Double = converter.convert(lenght, data)

}