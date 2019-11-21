package it.unibo.acdingnet.protelis.model

import it.unibo.acdingnet.protelis.mqtt.MqttMessageType

interface LoRaHeader {
    val header: String
}

interface LoRaPayload {
    fun payloadToString(): String
}

data class SensorData(val sensorType: SensorType, val sensorValue: Double)

interface LoRaSensorPayload: LoRaPayload {
    val sensorsData: List<SensorData>

    val time: Int

    val position: LatLongPosition?
}

interface LoRaUserPayload: LoRaSensorPayload {
    val destination: LatLongPosition?
}

data class EmptyLoRaHeader(override val header: String): LoRaHeader

data class LoRaSensorPayloadImpl(
    override val time: Int,
    override val sensorsData: List<SensorData>,//todo it will be only data without type
    override val position: LatLongPosition? = null
    ) : LoRaSensorPayload {

    //TODO
    override fun payloadToString(): String = "$sensorsData"
}

sealed class LoRaMessage<H: LoRaHeader, P : LoRaPayload>(header: LoRaHeader, payload: P): MqttMessageType

data class LoRaSensorMessage(val header: String = "", val payload: LoRaSensorPayloadImpl):
    LoRaMessage<EmptyLoRaHeader, LoRaSensorPayloadImpl>(
        EmptyLoRaHeader(header), payload)

data class LoRaUserPayloadImpl(
    override val time: Int,
    override val position: LatLongPosition,
    override val destination: LatLongPosition?,
    override val sensorsData: List<SensorData> = emptyList()
): LoRaUserPayload {
    override fun payloadToString(): String = "[" +
            "source: $position" +
            "destination: $destination" +
            "sensorData: $sensorsData" +
            "]"
}

data class LoRaUserMessage(val header: String = "", val payload: LoRaUserPayloadImpl):
    LoRaMessage<EmptyLoRaHeader, LoRaUserPayloadImpl>(
        EmptyLoRaHeader(header), payload)