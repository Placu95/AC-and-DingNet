package it.unibo.protelis.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.protelis.lang.datatype.DeviceUID
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager

data class MessageState(val payload: Map<CodePath, Any>)

class MQTTNetworkManager(val deviceUID: StringUID, serverAddress: String, applicationEUI: String, private var neighbors: Set<StringUID> = emptySet()): NetworkManager {

    private val baseTopic: String =  "application/$applicationEUI/node/"

    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()
    private var mqttClient = MqttClient(serverAddress, "", MemoryPersistence())
    private val gson: Gson = GsonBuilder().create()

    init {
        //TODO init subscription - test it
        neighbors.forEach{subscribeToMqtt(it)}
    }

    private fun getMqttStateTopicByDevice(deviceUID: StringUID): String = "$baseTopic${deviceUID.uid}/state"

    private fun subscribeToMqtt(deviceUID: StringUID) {
        mqttClient.subscribe(getMqttStateTopicByDevice(deviceUID)) { _, message ->
            val msg = gson.fromJson("$message", MessageState::class.java).payload
            messages += Pair(deviceUID, msg)
        }
    }

    override fun shareState(toSend: Map<CodePath, Any>): Unit =
        mqttClient.publish(getMqttStateTopicByDevice(deviceUID), toMqtt(MessageState(toSend)))

    //TODO to test this
    private fun toMqtt(message: MessageState): MqttMessage = MqttMessage(gson.toJson(message).toByteArray(Charsets.US_ASCII))

    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> = messages.apply { messages = emptyMap() }

    //TODO use mqtt here???
    fun setNeighbors(neighbors: Set<StringUID>) {
        //remove sensor not more neighbors
        this.neighbors.filter { !neighbors.contains(it) }.forEach{mqttClient.unsubscribe(getMqttStateTopicByDevice(it))}
        //add new neighbors
        neighbors.filter { !this.neighbors.contains(it) }.forEach{subscribeToMqtt(it)}
        this.neighbors = neighbors
    }
}