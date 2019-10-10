package it.unibo.acdingnet.protelis.executioncontext

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
    mqttAddress: String,
    netmgr: NetworkManager,
    randomSeed: Int = 1,
    execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ) :
    MQTTExecutionContext(device.deviceUID, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment),
    LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    override fun getCoordinates(): Tuple =  ArrayTupleImpl(device.position.latitude, device.position.longitude)
    override fun nbrVector(): Field<Tuple> = TODO("not implemented")
    override fun nbrRange(): Field<Double> = TODO("not implemented")
}