package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaUserMessage
import it.unibo.acdingnet.protelis.node.UserNode
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
    ): PositionedMQTTExecutionContext(userNode, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment) {

    private val destinatioPosition: LatLongPosition? = null

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
    override fun handleDefaultTopic(topic: String, message: String) {
        val msg = gson.fromJson(message, LoRaUserMessage::class.java)
        msg.payload.sensorsData.forEach{ execEnvironment.put(it.sensorType.type, it.sensorValue) }
        //TODO put environment variable to start the path
        //update position
        userNode.position = msg.payload.position
        //TODO create destination node
    }
}
