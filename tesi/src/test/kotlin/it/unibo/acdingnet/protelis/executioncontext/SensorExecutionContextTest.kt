package it.unibo.acdingnet.protelis.executioncontext

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.acdingnet.protelis.model.*
import it.unibo.acdingnet.protelis.mqtt.LoRaTransmissionWrapper
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttClientMock
import it.unibo.acdingnet.protelis.node.SensorNode
import it.unibo.acdingnet.protelis.util.Const
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram
import java.time.LocalTime

class EmptyProtelisProgram : ProtelisProgram {
    override fun compute(context: ExecutionContext?) = TODO("not implemented")
    override fun getCurrentValue(): Any = TODO("not implemented")
    override fun getName(): String = TODO("not implemented")
}

class SensorNodeSpy(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttClient: MqttClientBasicApi,
    position: LatLongPosition,
    sensorTypes: List<SensorType>
) : SensorNode(protelisProgram, sleepTime, sensorDeviceUID, applicationUID, mqttClient, position, sensorTypes) {

    fun spyExecContext() = executionContext
}

class SensorExecutionContextTest : StringSpec() {

    init {

        "when arrive a message from mqtt the execution context should update the node information" {
            val newPos = LatLongPosition(1.0, 1.0)
            val sensorData: Map<SensorType, List<Byte>> = mapOf(
                SensorType.PM10 to listOf<Byte>(50),
                SensorType.GPS to newPos.toBytes(),
                SensorType.NO2 to listOf<Byte>(1, 1))

            val sensors = sensorData.keys.toList()
            val node = SensorNodeSpy(
                EmptyProtelisProgram(),
                1L,
                StringUID("node1"),
                "test",
                MqttClientMock(),
                LatLongPosition.zero(),
                sensors
            )

            should { sensors.none { node.spyExecContext().executionEnvironment.has("$it") }}
            node.position shouldBe LatLongPosition.zero()

            //generate message
            val header = FrameHeader(emptyList(), 0,0, emptyList())
            val data: List<Byte> = sensorData.values
                .flatten()
                .toMutableList()
                .also { it.add(0, MessageType.SENSOR_VALUE.code)}
            val packet = LoRaWanPacket(1001L, 1L, false, 0.8, 15, data, emptyList(), header)
            val transmission = LoRaTransmission(1001L, 2L, 0.5, 60, 790, packet, "DATA_RATE1", LocalTime.MIN, 385.4,
                arrived = true,
                collided = false
            )
            //send message
            MqttClientMock().publish("application/${node.applicationUID}/node/${node.deviceUID.uid}/rx", LoRaTransmissionWrapper(transmission))
            //check value update
            node.spyExecContext().executionEnvironment.get(Const.IAQLEVEL_KEY) as Double shouldBe 82.125
            node.position shouldBe newPos
        }
    }
}