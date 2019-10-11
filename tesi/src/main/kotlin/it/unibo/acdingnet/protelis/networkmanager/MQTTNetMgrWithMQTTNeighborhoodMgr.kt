package it.unibo.acdingnet.protelis.networkmanager

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.neighborhood.NeighborhoodMessage
import it.unibo.acdingnet.protelis.neighborhood.NeighborhoodMessage.MessageType
import it.unibo.acdingnet.protelis.neighborhood.NewNeighborhoodMessage
import it.unibo.acdingnet.protelis.neighborhood.Node
import org.protelis.lang.datatype.impl.StringUID

class MQTTNetMgrWithMQTTNeighborhoodMgr(
    deviceUID: StringUID,
    serverAddress: String,
    applicationEUI: String,
    initialPosition: LatLongPosition
) : MQTTNetworkManager(deviceUID, serverAddress, applicationEUI, emptySet()) {

    private val publishOnTopic: String = "application/$applicationEUI/neighborhoodManager"
    private val subscribeOnTopic: String = "$baseTopic${deviceUID.uid}/neighborhood"

    init {
        mqttClient.subscribe(subscribeOnTopic) {
            _, message ->  setNeighbors(gson.fromJson(message, NewNeighborhoodMessage::class.java)
                .neighborhood.map { it.uid }.toSet())
        }

        mqttClient.publish(
            publishOnTopic,
            generateMessage(MessageType.ADD, deviceUID, initialPosition)
        )
    }

    fun changePosition(position: LatLongPosition) {
        mqttClient.publish(publishOnTopic, generateMessage(MessageType.UPDATE, deviceUID, position))
    }

    fun nodeDeleated() {
        mqttClient.publish(publishOnTopic, generateMessage(MessageType.LEAVE, deviceUID, LatLongPosition.zero()))
    }

    private fun generateMessage(type: MessageType, uid: StringUID, position: LatLongPosition) =
        generateMessage(type, Node(uid, position))

    private fun generateMessage(type: MessageType, node: Node) =
        gson.toJson(NeighborhoodMessage(type, node)).toByteArray(Charsets.US_ASCII)

}