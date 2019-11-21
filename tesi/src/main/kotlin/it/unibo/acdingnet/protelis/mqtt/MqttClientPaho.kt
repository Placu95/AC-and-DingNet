package it.unibo.acdingnet.protelis.mqtt

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import it.unibo.acdingnet.protelis.model.FrameHeader
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

class MqttClientPaho(mqttAddress: String, clientId: String) : MqttClientBasicApi{

    private val mqttClient = MqttClient(mqttAddress, clientId, MemoryPersistence())
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            FrameHeader::class.java,
            JsonDeserializer<FrameHeader> { json, _, _ ->
                FrameHeader(
                    Base64.getDecoder().decode((json as JsonObject).get("sourceAddress").asString).asList(),
                    json.get("fCnt").asInt,
                    json.get("fCtrl").asInt,
                    Base64.getDecoder().decode(json.get("fOpts").asString).asList()
                )
            })
        .create()

    override fun connect() {
        if (!mqttClient.isConnected) {
            mqttClient.connect(MqttConnectOptions().also { it.isCleanSession = true })
        }
    }

    override fun disconnect() {
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
        }
    }

    override fun publish(topic: String, message: MqttMessageType) = mqttClient.publish(topic, MqttMessage(gson.toJson(message).toByteArray(Charsets.US_ASCII)))

    override fun<T : MqttMessageType> subscribe(topicFilter: String, clazz: Class<T>, messageListener: (topic: String, message: T) -> Unit) {
           mqttClient.subscribe(topicFilter) {topic, message ->
               run { messageListener(topic, gson.fromJson("$message", clazz)) }
           }
    }

    override fun unsubscribe(topicFilter: String) = mqttClient.unsubscribe(topicFilter)
}

