package it.unibo.acdingnet.protelis.neighborhood

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.mqtt.MqttClientMock
import it.unibo.acdingnet.protelis.networkmanager.MQTTNetMgrWithMQTTNeighborhoodMgr
import org.protelis.lang.datatype.impl.StringUID

class MQTTNetMgrWithMQTTNeighborhoodMgrSpy(
    deviceUID: StringUID,
    mqttClient: MqttClientBasicApi,
    applicationEUI: String,
    initialPosition: LatLongPosition
) : MQTTNetMgrWithMQTTNeighborhoodMgr(deviceUID, mqttClient, applicationEUI, initialPosition) {

    fun getNodeNeighbors() = getNeighbors()
}

/**
 * Integration test between MQTTNetMgrWithMQTTNeighborhoodMgr and NeighborhoodManager
 */
class NeighborhoodIntegrationTest : StringSpec() {

    private val uid1 = StringUID("net1")
    private val uid2 = StringUID("net2")
    private val position1 = LatLongPosition.zero()
    private val neighborToPosition1 = LatLongPosition(0.0001, 0.0)
    private val notNeighborToPosition1 = LatLongPosition(100.0, 100.0)
    private val applicationID = "test"

    private fun getNeighborhoodManager() = NeighborhoodManager(applicationID, MqttClientMock(), 50.0)

    init {
        "if there is only one node, it should not have any neighbor" {
            val neighborhoodManager = getNeighborhoodManager()
            neighborhoodManager.neighborhood.keys.size shouldBe 0

            val net = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid1, MqttClientMock(), applicationID, position1)

            neighborhoodManager.neighborhood.keys.size shouldBe 1
            neighborhoodManager.neighborhood.filter { it.value.isEmpty() }.count() shouldBe 1
            should { net.getNodeNeighbors().isEmpty() }
        }

        "two nodes with distance lower than range should be neighbors" {
            val neighborhoodManager = getNeighborhoodManager()
            neighborhoodManager.neighborhood.keys.size shouldBe 0

            val net1 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid1, MqttClientMock(), applicationID, position1)
            val net2 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid2, MqttClientMock(), applicationID, neighborToPosition1)

            neighborhoodManager.neighborhood.keys.size shouldBe 2
            neighborhoodManager.neighborhood.filter { it.value.size == 1 }.count() shouldBe 2
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid1.uid && it.value.all { it.uid.uid == uid2.uid } }.count() shouldBe 1
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid2.uid && it.value.all { it.uid.uid == uid1.uid } }.count() shouldBe 1
            should { net1.getNodeNeighbors().map { it.uid }.contains(uid2.uid) }
            should { net2.getNodeNeighbors().map { it.uid }.contains(uid1.uid) }
        }

        "two nodes with distance lower than range should not be neighbors" {
            val neighborhoodManager = getNeighborhoodManager()
            neighborhoodManager.neighborhood.keys.size shouldBe 0

            val net1 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid1, MqttClientMock(), applicationID, position1)
            val net2 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid2, MqttClientMock(), applicationID, notNeighborToPosition1)

            neighborhoodManager.neighborhood.keys.size shouldBe 2
            neighborhoodManager.neighborhood.filter { it.value.size == 0 }.count() shouldBe 2
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid1.uid && it.value.none { it.uid.uid == uid2.uid } }.count() shouldBe 1
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid2.uid && it.value.none { it.uid.uid == uid1.uid } }.count() shouldBe 1
            should { net1.getNodeNeighbors().map { it.uid }.isEmpty() }
            should { net2.getNodeNeighbors().map { it.uid }.isEmpty() }
        }

        "when a node leave the system, the neighborhoodManager should delete the node" {
            val neighborhoodManager = getNeighborhoodManager()
            neighborhoodManager.neighborhood.keys.size shouldBe 0

            val net1 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid1, MqttClientMock(), applicationID, position1)
            val net2 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid2, MqttClientMock(), applicationID, neighborToPosition1)
            should { net1.getNodeNeighbors().map { it.uid }.contains(uid2.uid) }
            net2.nodeDeleted()

            neighborhoodManager.neighborhood.keys.size shouldBe 1
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid1.uid && it.value.isEmpty() }.count() shouldBe 1
            should { net1.getNodeNeighbors().map { it.uid }.isEmpty() }
        }

        "when a node's neighbors change position and move outside the range of the node should exit from the neighborhood" {
            val neighborhoodManager = getNeighborhoodManager()
            neighborhoodManager.neighborhood.keys.size shouldBe 0

            val net1 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid1, MqttClientMock(), applicationID, position1)
            val net2 = MQTTNetMgrWithMQTTNeighborhoodMgrSpy(uid2, MqttClientMock(), applicationID, neighborToPosition1)

            neighborhoodManager.neighborhood.keys.size shouldBe 2
            neighborhoodManager.neighborhood.filter { it.value.size == 1 }.count() shouldBe 2
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid1.uid && it.value.all { it.uid.uid == uid2.uid } }.count() shouldBe 1
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid2.uid && it.value.all { it.uid.uid == uid1.uid } }.count() shouldBe 1
            should { net1.getNodeNeighbors().map { it.uid }.contains(uid2.uid) }
            should { net2.getNodeNeighbors().map { it.uid }.contains(uid1.uid) }

            net2.changePosition(notNeighborToPosition1)

            neighborhoodManager.neighborhood.keys.size shouldBe 2
            neighborhoodManager.neighborhood.filter { it.value.size == 0 }.count() shouldBe 2
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid1.uid && it.value.none { it.uid.uid == uid2.uid } }.count() shouldBe 1
            neighborhoodManager.neighborhood.filter { it.key.uid.uid == uid2.uid && it.value.none { it.uid.uid == uid1.uid } }.count() shouldBe 1
            should { net1.getNodeNeighbors().map { it.uid }.isEmpty() }
            should { net2.getNodeNeighbors().map { it.uid }.isEmpty() }
        }
    }

}