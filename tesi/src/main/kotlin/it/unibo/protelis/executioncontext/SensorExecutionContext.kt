package it.unibo.protelis.executioncontext

import it.unibo.protelis.model.LoRaSensorMessage
import it.unibo.protelis.node.SensorNode
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.SimpleExecutionEnvironment

class SensorExecutionContext(
    private val sensorNode: SensorNode,
    applicationUID: String,
    mqttAddress: String,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ): PositionedMQTTExecutionContext(sensorNode.deviceUID, sensorNode.position, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment) {

    override fun instance(): SensorExecutionContext =
        SensorExecutionContext(
            sensorNode,
            applicationUID,
            mqttAddress,
            netmgr,
            randomSeed,
            execEnvironment
        )

    override fun handleDefaultTopic(topic: String, message: MqttMessage) {
        gson.fromJson("$message", LoRaSensorMessage::class.java).payload.sensorsData.forEach{
            execEnvironment.put(it.sensorType.type, it.sensorValue)
        }
    }
}