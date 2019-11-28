package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.model.MessageType
import it.unibo.acdingnet.protelis.model.SensorType
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.SensorNode
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
    ): PositionedMQTTExecutionContext(sensorNode, applicationUID, mqttClient, netmgr, randomSeed, execEnvironment) {

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
                    else -> execEnvironment.put("$it", it.convertToDouble(payload))
                }
            }
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