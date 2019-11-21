package it.unibo.acdingnet.protelis.mqtt

class MqttClientMock: MqttClientBasicApi {

    private val subscribed: MutableMap<String, MutableList<MqttMessageConsumer<*>>> = mutableMapOf()

    override fun connect() { MqttBrokerMock.connect(this) }

    override fun disconnect() { MqttBrokerMock.disconnect(this) }

    override fun publish(topic: String, message: MqttMessageType) {
        MqttBrokerMock.publish(topic, message)
    }

    override fun<T : MqttMessageType> subscribe(topicFilter: String, clazz: Class<T>, messageListener: (topic: String, message: T) -> Unit) {
        if (!subscribed.containsKey(topicFilter)) {
            subscribed += topicFilter to mutableListOf()
            MqttBrokerMock.subscribe(this, topicFilter)
        }
        subscribed[topicFilter]!! += MqttMessageConsumer(clazz, messageListener)
    }

    override fun unsubscribe(topicFilter: String) {
        subscribed -= topicFilter
        MqttBrokerMock.unsubscribe(this, topicFilter)
    }

    fun dispatch(topic: String, message: MqttMessageType) {
        subscribed[topic]?.let { it.forEach { it.accept(topic, message) } }
    }

    private data class MqttMessageConsumer<T: MqttMessageType> (val clazz: Class<T>, val consumer: (String, T) -> Unit) {
        fun accept(topic: String, message: MqttMessageType): Unit = consumer.invoke(topic, clazz.cast(message))
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

    fun publish(topic: String, message: MqttMessageType) {
        clientSubscribed.filter { it.value.contains(topic) }.forEach{ it.key.dispatch(topic, message)}
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