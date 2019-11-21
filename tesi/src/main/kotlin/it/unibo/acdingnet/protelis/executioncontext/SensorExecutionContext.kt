package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.model.SensorType
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.SensorNode
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

    override fun handleDefaultTopic(topic: String, message: LoRaTransmission) {
        println(message)
        val payload = message.content.payload.toMutableList()
        sensorNode.sensorTypes.forEach {
            when(it) {
                SensorType.GPS -> consumeGPSData(payload)
                else -> execEnvironment.put(it.type, it.convertToDouble(payload))
            }
        }
    }

    private fun consumeGPSData(payload: MutableList<Byte>) {
        var count = 0
        val pair = payload.partition { count++ < SensorType.GPS.lenght }
        payload.removeAll(pair.first)
        val buffer = ByteBuffer.wrap(pair.first.toByteArray())
        sensorNode.position = LatLongPosition(buffer.float.toDouble(), buffer.getFloat(4).toDouble())//4 is number of bytes used for float type
    }
}