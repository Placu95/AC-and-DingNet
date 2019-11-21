package it.unibo.acdingnet.protelis.mqtt

interface MqttClientBasicApi {

    fun connect()

    fun disconnect()

    fun publish(topic: String, message: MqttMessageType)

    fun <T : MqttMessageType> subscribe(topicFilter: String, clazz: Class<T>, messageListener: (topic: String, message: T) -> Unit)

    fun unsubscribe(topicFilter: String)
}