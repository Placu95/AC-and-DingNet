package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.model.MessageType
import it.unibo.acdingnet.protelis.model.SensorType
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.SensorNode
import it.unibo.acdingnet.protelis.util.Const
import it.unibo.acdingnet.protelis.util.SIZE_BYTES
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.SimpleExecutionEnvironment
import java.nio.ByteBuffer

open class SensorExecutionContext(
    private val sensorNode: SensorNode,
    applicationUID: String,
    mqttClient: MqttClientBasicApi,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ): MQTTPositionedExecutionContext(sensorNode.deviceUID, sensorNode.position, applicationUID, mqttClient, netmgr, randomSeed, execEnvironment) {

    private var sensorsValue: Map<SensorType, Double> = emptyMap()

    override fun instance(): SensorExecutionContext =
        SensorExecutionContext(
            sensorNode,
            applicationUID,
            mqttClient,
            netmgr,
            randomSeed,
            execEnvironment
        )

    override fun handleDeviceTransmission(topic: String, message: LoRaTransmission) {
        val payload = message.content.payload.toMutableList()
        if (payload.isNotEmpty() && payload[0] == MessageType.SENSOR_VALUE.code) {
            payload.removeAt(0)
            sensorNode.sensorTypes.forEach {
                when (it) {
                    SensorType.GPS -> sensorNode.position = consumeGPSData(payload)
                    else -> sensorsValue = sensorsValue.plus(Pair(it, it.convertToDouble(payload)))
                }
            }
            sensorsValue
                .map { sensor -> IAQCalculator.computeIaqLevel(sensor.key, sensor.value) }
                .max()
                ?.let { value -> execEnvironment.put(Const.ProtelisEnv.IAQLEVEL_KEY, value) }
        }
    }

    protected fun consumeGPSData(payload: MutableList<Byte>): LatLongPosition {
        var count = 0
        val pair = payload.partition { count++ < SensorType.GPS.lenght }
        payload.removeAll(pair.first)
        val buffer = ByteBuffer.wrap(pair.first.toByteArray())
        return LatLongPosition(buffer.float.toDouble(), buffer.getFloat(Float.SIZE_BYTES).toDouble())
    }
}

object IAQCalculator {

    private val iaqLevel: List<Pair<Int, Int>> = SensorType.IAQ.levels
    private val sensorsLevel: Map<SensorType, List<Pair<Int, Int>>> = mapOf(
        SensorType.PM10 to SensorType.PM10.levels,
        SensorType.NO2 to SensorType.NO2.levels
    )

    fun computeIaqLevel(sensorType: SensorType, value: Double): Double {
        return sensorsLevel[sensorType]
            ?.mapIndexed { i, v -> Pair(i,v) }
            ?.find { it.second.first <= value && value < it.second.second }
            ?.let {
                val c = it.second
                val iaq = iaqLevel[it.first]
                1.0 * (iaq.second - iaq.first) / (c.second - c.first) * (value - c.first) + iaq.first
            } !!.toDouble()
    }
}