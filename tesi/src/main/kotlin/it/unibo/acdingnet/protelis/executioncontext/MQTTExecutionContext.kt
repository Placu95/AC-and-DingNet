package it.unibo.acdingnet.protelis.executioncontext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttClientPaho
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

    protected val mqttClient: MqttClientBasicApi = MqttClientPaho(mqttAddress, "").also {
        it.connect()
        it.subscribe(receiveTopic, this::handleDefaultTopic)
    }

    protected abstract fun handleDefaultTopic(topic: String, message: String)
    //endregion

    override fun nextRandomDouble(): Double = randomGenerator.nextDouble()
    override fun getDeviceUID(): DeviceUID = _deviceUID
    override fun getCurrentTime(): Number = LocalDateTime.now().second
}