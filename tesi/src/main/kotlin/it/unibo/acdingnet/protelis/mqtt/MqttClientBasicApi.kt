package it.unibo.acdingnet.protelis.mqtt

interface MqttClientBasicApi {

    fun connect()

    fun disconnect()

    fun publish(topic: String, message: String)

    fun publish(topic: String, message: ByteArray)

    fun subscribe(topicFilter: String, messageListener: (topic: String, message: String) -> Unit)

    fun subscribeToByteArray(topicFilter: String, messageListener: (topic: String, message: ByteArray) -> Unit)

    fun unsubscribe(topicFilter: String)
}