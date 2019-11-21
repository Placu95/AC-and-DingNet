package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttMessageType
import it.unibo.acdingnet.protelis.node.UserNode
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.SimpleExecutionEnvironment

class UserExecutionContext(
    private val userNode: UserNode,
    applicationUID: String,
    mqttClient: MqttClientBasicApi,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ): PositionedMQTTExecutionContext(userNode, applicationUID, mqttClient, netmgr, randomSeed, execEnvironment) {

    private val destinatioPosition: LatLongPosition? = null

    override fun instance(): UserExecutionContext =
        UserExecutionContext(
            userNode,
            applicationUID,
            mqttClient,
            netmgr,
            randomSeed,
            execEnvironment
        )

    //TODO
    override fun handleDefaultTopic(topic: String, message: LoRaTransmission) {
        println(message)
        mqttClient.publish(topic.replace("rx", "tx"), Test(listOf(-1)))
        /*
        val msg = gson.fromJson(message, LoRaUserMessage::class.java)
        msg.payload.sensorsData.forEach{ execEnvironment.put(it.sensorType.type, it.sensorValue) }
        //TODO put environment variable to start the path
        //update position
        userNode.position = msg.payload.position
        //TODO create destination node

         */
    }
}

data class Test(val data: List<Byte>, val macCommands: List<String> = emptyList()): MqttMessageType
