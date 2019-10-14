package it.unibo.acdingnet.protelis.networmanager

import gnu.trove.list.array.TIntArrayList
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.acdingnet.protelis.mqtt.MqttClientMock
import it.unibo.acdingnet.protelis.networkmanager.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.CodePath
import org.protelis.vm.impl.DefaultTimeEfficientCodePath

class MQTTNetworkManagerTest: StringSpec() {

    init {
        "the state should arrive to the neighbors" {
            val netId1 = StringUID("net1")
            val netId2 = StringUID("net2")
            val net1 = MQTTNetworkManager(netId1, MqttClientMock(), "test", setOf(netId2))
            val net2 = MQTTNetworkManager(netId2, MqttClientMock(), "test", setOf(netId1))

            val state = mapOf<CodePath, Int>(DefaultTimeEfficientCodePath(TIntArrayList()) to 0)
            net1.shareState(state)

            net2.neighborState[netId1] shouldBe state
        }
    }
}