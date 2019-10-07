package it.unibo.protelis.sensor

import it.unibo.protelis.model.LoRaSensorMessage
import it.unibo.protelis.model.SensorNode
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

class SensorExecutionContext(
    private val sensorNode: SensorNode,
    applicationUID: String,
    mqttAddress: String,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    private val execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ): MQTTExecutionContext(sensorNode.sensorDeviceUID, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment),
    LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    private val _coordinates: Tuple by lazy {
        ArrayTupleImpl(sensorNode.position.latitude, sensorNode.position.longitude)
    }

    override fun getCoordinates(): Tuple = _coordinates

    override fun nbrVector(): Field<Tuple> = TODO("not implemented")

    override fun nbrRange(): Field<Double> = TODO("not implemented")

    override fun instance(): SensorExecutionContext = SensorExecutionContext(sensorNode, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment)

    override fun handleDefaultTopic(topic: String, message: MqttMessage) {
        gson.fromJson("$message", LoRaSensorMessage::class.java).payload.sensorsData.forEach{
            execEnvironment.put(it.sensorType.type, it.sensorValue)
        }
    }
}