package it.unibo.protelis.executioncontext

import it.unibo.protelis.model.LoRaUserMessage
import it.unibo.protelis.node.UserNode
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.SimpleExecutionEnvironment

class UserExecutionContext(
        private val userNode: UserNode,
        applicationUID: String,
        mqttAddress: String,
        netmgr: NetworkManager,
        randomSeed: Int = 1,
        execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ): PositionedMQTTExecutionContext(userNode.deviceUID, userNode.position, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment) {

    override fun instance(): UserExecutionContext =
        UserExecutionContext(
            userNode,
            applicationUID,
            mqttAddress,
            netmgr,
            randomSeed,
            execEnvironment
        )

    //TODO
    override fun handleDefaultTopic(topic: String, message: MqttMessage) {
        val msg = gson.fromJson("$message", LoRaUserMessage::class.java)
        msg.payload.sensorsData.forEach{ execEnvironment.put(it.sensorType.type, it.sensorValue) }
        //TODO put environment variable to start the path
        //TODO create destination node
    }
}