package it.unibo.protelis.user

import it.unibo.protelis.model.LoRaUserMessage
import it.unibo.protelis.model.UserNode
import it.unibo.protelis.utils.MQTTExecutionContext
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.protelis.lang.datatype.Field
import org.protelis.lang.datatype.Tuple
import org.protelis.lang.datatype.impl.ArrayTupleImpl
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.LocalizedDevice
import org.protelis.vm.NetworkManager
import org.protelis.vm.SpatiallyEmbeddedDevice
import org.protelis.vm.impl.SimpleExecutionEnvironment

class UserExecutionContext(
    private val userNode: UserNode,
    applicationUID: String,
    mqttAddress: String,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    private val execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
): MQTTExecutionContext(userNode.userDeviceUID, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment),
    LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    private val _coordinates: Tuple by lazy {
        ArrayTupleImpl(userNode.position.latitude, userNode.position.longitude)
    }

    override fun getCoordinates(): Tuple = _coordinates

    override fun nbrVector(): Field<Tuple> = TODO("not implemented")

    override fun nbrRange(): Field<Double> = TODO("not implemented")

    override fun instance(): UserExecutionContext = UserExecutionContext(userNode, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment)

    //TODO
    override fun handleDefaultTopic(topic: String, message: MqttMessage) {
        val msg = gson.fromJson("$message", LoRaUserMessage::class.java)
        msg.payload.sensorsData.forEach{ execEnvironment.put(it.sensorType.type, it.sensorValue) }
        //TODO put environment variable to start the path
        //TODO create destination node
    }
}