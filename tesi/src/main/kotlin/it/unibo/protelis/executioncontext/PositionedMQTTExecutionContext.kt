package it.unibo.protelis.executioncontext

import it.unibo.protelis.model.GPSPosition
import org.protelis.lang.datatype.Field
import org.protelis.lang.datatype.Tuple
import org.protelis.lang.datatype.impl.ArrayTupleImpl
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.LocalizedDevice
import org.protelis.vm.NetworkManager
import org.protelis.vm.SpatiallyEmbeddedDevice
import org.protelis.vm.impl.SimpleExecutionEnvironment

abstract class PositionedMQTTExecutionContext(
        deviceUID: StringUID,
        val nodePosition: GPSPosition,
        applicationUID: String,
        mqttAddress: String,
        netmgr: NetworkManager,
        randomSeed: Int = 1,
        execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ) :
    MQTTExecutionContext(deviceUID, applicationUID, mqttAddress, netmgr, randomSeed, execEnvironment),
    LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    private val _coordinates: Tuple by lazy {
        ArrayTupleImpl(nodePosition.latitude, nodePosition.longitude)
    }

    override fun getCoordinates(): Tuple = _coordinates
    override fun nbrVector(): Field<Tuple> = TODO("not implemented")
    override fun nbrRange(): Field<Double> = TODO("not implemented")
}