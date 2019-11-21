package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import it.unibo.acdingnet.protelis.node.GenericNode
import org.protelis.lang.datatype.Field
import org.protelis.lang.datatype.Tuple
import org.protelis.lang.datatype.impl.ArrayTupleImpl
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.LocalizedDevice
import org.protelis.vm.NetworkManager
import org.protelis.vm.SpatiallyEmbeddedDevice
import org.protelis.vm.impl.SimpleExecutionEnvironment

abstract class PositionedMQTTExecutionContext(
    private val device: GenericNode, //TODO improve information hiding
    applicationUID: String,
    mqttClient: MqttClientBasicApi,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ) :
    MQTTExecutionContext(device.deviceUID, applicationUID, mqttClient, netmgr, randomSeed, execEnvironment),
    LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    override fun getCoordinates(): Tuple =  ArrayTupleImpl(device.position.getLatitude(), device.position.getLongitude())
    override fun nbrVector(): Field<Tuple> = TODO("not implemented")
    override fun nbrRange(): Field<Double> = buildField({ it.distanceTo(device.position) }, device.position)
}