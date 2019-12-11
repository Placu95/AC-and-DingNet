package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.model.MessageType
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.DestinationNode
import it.unibo.acdingnet.protelis.node.UserNode
import it.unibo.acdingnet.protelis.util.Const
import org.protelis.lang.datatype.impl.StringUID
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
    ): SensorExecutionContext(userNode, applicationUID, mqttClient, netmgr, randomSeed, execEnvironment) {

    private var destinationPosition: LatLongPosition? = null

    override fun instance(): UserExecutionContext =
        UserExecutionContext(
            userNode,
            applicationUID,
            mqttClient,
            netmgr,
            randomSeed,
            execEnvironment
        )

    override fun handleDeviceTransmission(topic: String, message: LoRaTransmission) {
        val payload = message.content.payload
        if (payload.isNotEmpty()) {
            when (payload[0]) {
                MessageType.SENSOR_VALUE.code -> super.handleDeviceTransmission(topic, message)
                MessageType.REQUEST_PATH.code -> handleRequestPath(payload.toMutableList().also{ it.removeAt(0) })
                else -> throw IllegalArgumentException("message type not supported")
            }
        }
    }

    //TODO
    private fun handleRequestPath(mutPayload: MutableList<Byte>) {
        //TODO put environment variable to start the path
        execEnvironment.put(Const.ProtelisEnv.SOURCE_KEY, true)
        //update position
        userNode.position = consumeGPSData(mutPayload)
        //TODO create destination node
        destinationPosition = consumeGPSData(mutPayload).also {
            DestinationNode(userNode.protelisProgram, userNode.sleepTime, StringUID(""), applicationUID, mqttClient, it)
        }
    }
}