package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LoRaSensorMessage
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.SensorNode
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.SimpleExecutionEnvironment

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

    override fun handleDefaultTopic(topic: String, message: String) {
        val msg = gson.fromJson(message, LoRaSensorMessage::class.java)
        msg.payload.sensorsData.forEach{ execEnvironment.put(it.sensorType.type, it.sensorValue) } //todo use order of the list check time property
        msg.payload.position?.let { sensorNode.position = it }
    }
}