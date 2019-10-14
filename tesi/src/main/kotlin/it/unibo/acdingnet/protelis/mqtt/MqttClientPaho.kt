package it.unibo.acdingnet.protelis.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClientPaho(mqttAddress: String, clientId: String) : MqttClientBasicApi{

    private val mqttClient = MqttClient(mqttAddress, clientId, MemoryPersistence())

    override fun connect() = mqttClient.connect(MqttConnectOptions().also { it.isCleanSession = true })

    override fun disconnect() = mqttClient.disconnect()

    override fun publish(topic: String, message: String) = publish(topic, message.toByteArray(Charsets.US_ASCII))

    override fun publish(topic: String, message: ByteArray) = mqttClient.publish(topic, MqttMessage(message))

    override fun subscribe(topicFilter: String, messageListener: (topic: String, message: String) -> Unit) =
        mqttClient.subscribe(topicFilter) {topic, message -> messageListener(topic, "$message") }

    override fun subscribeToByteArray(topicFilter: String, messageListener: (topic: String, message: ByteArray) -> Unit) =
        mqttClient.subscribe(topicFilter) {topic, message -> messageListener(topic, message.payload) }

    override fun unsubscribe(topicFilter: String) = mqttClient.unsubscribe(topicFilter)
}