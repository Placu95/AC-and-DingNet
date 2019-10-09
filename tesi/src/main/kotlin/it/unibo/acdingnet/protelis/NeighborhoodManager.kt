package it.unibo.acdingnet.protelis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.unibo.acdingnet.protelis.model.LatLongPosition
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.protelis.lang.datatype.impl.StringUID

class NeighborhoodMessage(val type: MessageType, val node: Node) {

    enum class MessageType {ADD, UPDATE, LEAVE}
}

class NewNeighborhoodMessage(val neighborhood: Set<Node>)

class Node(val uid: StringUID, var position: LatLongPosition) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass == other?.javaClass && uid == (other as Node).uid) return true
        return false
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}

/**
 * Simple NeighborhoodManager that suppose symmetric distance between two position
 */
class NeighborhoodManager(val applicationUID: String, val mqttAddress: String, val range: Double) {

    val neighborhood: MutableMap<Node, Set<Node>> = mutableMapOf()

    private val gson: Gson = GsonBuilder().create()
    private val subscribedTopic: String = "application/$applicationUID/neighborhoodManager"
    private val mqttClient = MqttClient(mqttAddress, "", MemoryPersistence()).also {
        it.connect(MqttConnectOptions().also { it.isCleanSession = true })
        it.subscribe(subscribedTopic) {topic, message ->
            val msg = gson.fromJson("$message", NeighborhoodMessage::class.java)
            when(msg.type) {
                NeighborhoodMessage.MessageType.ADD -> addNode(msg.node)
                NeighborhoodMessage.MessageType.LEAVE -> removeNode(msg.node)
                NeighborhoodMessage.MessageType.UPDATE -> updateNode(msg.node)
            }}
    }

    private fun addNode(node: Node) {
        val nodeNeighborhood = neighborhood.keys.filter { node.position.distanceTo(it.position) < range }.toSet()
        neighborhood += (node to nodeNeighborhood)
        nodeNeighborhood.forEach{ neighborhood[it]!!.plus(node) }
        //TODO send reply with neighbors
        //TODO send update to all modified node
    }

    private fun removeNode(node: Node) {
        val nodeNeighborhood = neighborhood[node]
        neighborhood -= node
        nodeNeighborhood!!.forEach{ neighborhood[it]!!.minus(node) }
        //TODO send update to all modified node
    }

    private fun updateNode(node: Node) {
        val newNeighborhood = neighborhood.keys.filter { node.position.distanceTo(it.position) < range }.toSet()
        val neighborsAdded = newNeighborhood.filter { !neighborhood[node]!!.contains(it) }.also {
            it.forEach{ neighborhood[it]!!.plus(node) }
        }
        val neighborsRemoved = neighborhood[node]!!.filter { !newNeighborhood.contains(it) }.also {
            it.forEach{ neighborhood[it]!!.minus(node) }
        }
        //TODO send update to node, neighborsAdded, neighborsRemoved
    }

    //TODO test
    private fun sendUpdateNeighborhood(node: Node, neighborhood: Set<Node>) {
        mqttClient.publish(
            getNodeTopic(node),
            MqttMessage(gson.toJson(NewNeighborhoodMessage(neighborhood)).toByteArray(Charsets.US_ASCII)))
    }

    private fun getNodeTopic(node: Node) = "application/$applicationUID/node/${node.uid}/neighborhood"
}