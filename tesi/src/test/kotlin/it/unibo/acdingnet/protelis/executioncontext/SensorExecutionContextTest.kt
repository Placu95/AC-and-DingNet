package it.unibo.acdingnet.protelis.executioncontext

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.acdingnet.protelis.model.*
import it.unibo.acdingnet.protelis.mqtt.LoRaTransmissionWrapper
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttClientMock
import it.unibo.acdingnet.protelis.node.SensorNode
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
            val node = SensorNodeSpy(
                EmptyProtelisProgram(),
                1L,
                StringUID("node1"),
                "test",
                MqttClientMock(),
                LatLongPosition.zero(),
                listOf(SensorType.SOOT)
            )

            should { !node.spyExecContext().executionEnvironment.has(SensorType.SOOT.type) }
            node.position shouldBe LatLongPosition.zero()

            //generate message
            val header = FrameHeader(emptyList(), 0,0, emptyList())
            val packet = LoRaWanPacket(1001L, 1L, false, 0.8, 15, emptyList(), emptyList(), header)
            val transmission = LoRaTransmission(1001L, 2L, 0.5, 60, 790, packet, "DATA_RATE1", LocalTime.MIN, 385.4,
                arrived = true,
                collided = false
            )
            //send message
            MqttClientMock().publish("application/${node.applicationUID}/node/${node.deviceUID.uid}/rx", LoRaTransmissionWrapper(transmission))
            //check value update
            //node.spyExecContext().executionEnvironment.get(SensorType.SOOT.type) as Double shouldBe data.sensorValue
            //node.position shouldBe newPos
        }
    }
}