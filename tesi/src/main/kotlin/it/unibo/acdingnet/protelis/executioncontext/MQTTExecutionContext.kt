package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LoRaTransmission
import it.unibo.acdingnet.protelis.mqtt.LoRaTransmissionWrapper
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
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
    protected val mqttClient: MqttClientBasicApi,
    val netmgr: NetworkManager,
    val randomSeed: Int = 1,
    protected val execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
) : AbstractExecutionContext<MQTTExecutionContext>(execEnvironment, netmgr) {

    private val randomGenerator = Random(randomSeed)

    //region MQTT
    private val baseTopic: String = "application/$applicationUID/node/${_deviceUID.uid}/"
    private val receiveTopic: String = "${baseTopic}rx"

    init {
        mqttClient.connect()
        mqttClient.subscribe(receiveTopic, LoRaTransmissionWrapper::class.java) {t, m -> handleDeviceTransmission(t, m.transmission)}
    }

    protected abstract fun handleDeviceTransmission(topic: String, message: LoRaTransmission)
    //endregion

    override fun nextRandomDouble(): Double = randomGenerator.nextDouble()
    override fun getDeviceUID(): DeviceUID = _deviceUID
    override fun getCurrentTime(): Number = LocalDateTime.now().second
}