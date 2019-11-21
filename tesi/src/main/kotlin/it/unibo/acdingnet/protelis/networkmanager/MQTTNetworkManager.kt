package it.unibo.acdingnet.protelis.networkmanager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttClientPaho
import it.unibo.acdingnet.protelis.mqtt.MqttMessageType
import org.protelis.lang.datatype.DeviceUID
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.CodePath
import org.protelis.vm.NetworkManager
import java.io.Serializable

data class MessageState(val payload: Map<CodePath, Any>): Serializable, MqttMessageType

open class MQTTNetworkManager(
    val deviceUID: StringUID,
    protected var mqttClient: MqttClientBasicApi,
    applicationEUI: String,
    private var neighbors: Set<StringUID> = emptySet()): NetworkManager {

    constructor(deviceUID: StringUID, serverAddress: String, applicationEUI: String, neighbors: Set<StringUID> = emptySet()) :
        this(deviceUID, MqttClientPaho(serverAddress, ""), applicationEUI, neighbors)

    protected val baseTopic: String =  "application/$applicationEUI/node/"

    private var messages: Map<DeviceUID, Map<CodePath, Any>> = emptyMap()

    protected val gson: Gson = GsonBuilder().create()

    init {
        mqttClient.connect()
        neighbors.forEach{subscribeToMqtt(it)}
    }

    private fun getMqttStateTopicByDevice(deviceUID: StringUID): String = "$baseTopic${deviceUID.uid}/state"

    protected fun subscribeToMqtt(deviceUID: StringUID) {
        mqttClient.subscribe(getMqttStateTopicByDevice(deviceUID), MessageState::class.java) { _, message ->
            messages += deviceUID to message.payload
        }
    }

    override fun shareState(toSend: Map<CodePath, Any>): Unit = mqttClient.publish(getMqttStateTopicByDevice(deviceUID), MessageState(toSend))

    override fun getNeighborState(): Map<DeviceUID, Map<CodePath, Any>> = messages.apply { messages = emptyMap() }

    fun setNeighbors(neighbors: Set<StringUID>) {
        //remove sensor not more neighbors
        this.neighbors.filter { !neighbors.contains(it) }.forEach{mqttClient.unsubscribe(getMqttStateTopicByDevice(it))}
        //add new neighbors
        neighbors.filter { !this.neighbors.contains(it) }.forEach{subscribeToMqtt(it)}
        this.neighbors = neighbors
    }

    protected fun getNeighbors() = neighbors
}