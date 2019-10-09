package it.unibo.acdingnet.protelis.executioncontext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.protelis.lang.datatype.DeviceUID
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.NetworkManager
import org.protelis.vm.impl.AbstractExecutionContext
import org.protelis.vm.impl.SimpleExecutionEnvironment
import java.time.LocalDateTime
import kotlin.random.Random

abstract class MQTTExecutionContext(
    private val _deviceUID: StringUID,
    val applicationUID: String,
    val mqttAddress: String,
    val netmgr: NetworkManager,
    val randomSeed: Int = 1,
    protected val execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
) : AbstractExecutionContext<MQTTExecutionContext>(execEnvironment, netmgr) {

    private val randomGenerator = Random(randomSeed)

    //region MQTT
    protected val gson: Gson = GsonBuilder().create()
    private val baseTopic: String = "application/$applicationUID/node/${_deviceUID.uid}/"
    private val receiveTopic: String = "${baseTopic}rx"

    protected val mqttClient = MqttClient(mqttAddress, "", MemoryPersistence()).also {
        it.connect(MqttConnectOptions().also { it.isCleanSession = true })
        it.subscribe(receiveTopic) {topic, message -> handleDefaultTopic(topic, message)}
    }

    protected abstract fun handleDefaultTopic(topic: String, message: MqttMessage)
    //endregion

    override fun nextRandomDouble(): Double = randomGenerator.nextDouble()
    override fun getDeviceUID(): DeviceUID = _deviceUID
    override fun getCurrentTime(): Number = LocalDateTime.now().second
}