package it.unibo.acdingnet.protelis.mqtt

class MqttClientMock: MqttClientBasicApi {

    private val subscribed: MutableMap<String, MutableList<(String, String) -> Unit>> = mutableMapOf()

    override fun connect() { MqttBrokerMock.connect(this) }

    override fun disconnect() { MqttBrokerMock.disconnect(this) }

    override fun publish(topic: String, message: String) { MqttBrokerMock.publish(topic, message) }

    override fun publish(topic: String, message: ByteArray) {
        publish(topic, String(message))
    }

    override fun subscribe(topicFilter: String, messageListener: (topic: String, message: String) -> Unit) {
        if (!subscribed.containsKey(topicFilter)) {
            subscribed += topicFilter to mutableListOf()
            MqttBrokerMock.subscribe(this, topicFilter)
        }
        subscribed[topicFilter]!! += messageListener
    }

    override fun unsubscribe(topicFilter: String) {
        subscribed -= topicFilter
        MqttBrokerMock.unsubscribe(this, topicFilter)
    }

    fun dispatch(topic: String, message: String) {
        subscribed[topic]?.let { it.forEach { it(topic, message) } }
    }
}

object MqttBrokerMock {

    private val clientSubscribed: MutableMap<MqttClientMock, MutableList<String>> = mutableMapOf()

    fun connect(instance: MqttClientMock) {
        clientSubscribed += instance to mutableListOf()
    }

    fun disconnect(instance: MqttClientMock) {
        clientSubscribed -= instance
    }

    fun publish(topic: String, message: String) {
        clientSubscribed.filter { it.value.contains(topic) }.forEach{ it.key.dispatch(topic, message)}
    }

    fun publish(topic: String, message: ByteArray) {
        publish(topic, String(message))
    }

    fun subscribe(instance: MqttClientMock, topicFilter: String) {
        if (!clientSubscribed.containsKey(instance)) {
            throw IllegalStateException("client not connected")
        }
        clientSubscribed[instance]!! += topicFilter
    }

    fun unsubscribe(instance: MqttClientMock, topicFilter: String) {
        clientSubscribed[instance]?.let{it -= topicFilter}
    }

}