package it.unibo.acdingnet.protelis.executioncontext

import it.unibo.acdingnet.protelis.model.LatLongPosition
import org.protelis.lang.datatype.DeviceUID
import org.protelis.lang.datatype.Field
import org.protelis.lang.datatype.Tuple
import org.protelis.lang.datatype.impl.ArrayTupleImpl
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionEnvironment
import org.protelis.vm.LocalizedDevice
import org.protelis.vm.NetworkManager
import org.protelis.vm.SpatiallyEmbeddedDevice
import org.protelis.vm.impl.AbstractExecutionContext
import org.protelis.vm.impl.SimpleExecutionEnvironment
import java.time.LocalDateTime
import kotlin.random.Random

class DestinationExecutionContext(
    private val _deviceUID: StringUID,
    private val nodePosition: LatLongPosition,
    private val netmgr: NetworkManager,
    private val randomSeed: Int = 1,
    private val execEnvironment: ExecutionEnvironment = SimpleExecutionEnvironment()
    ) : AbstractExecutionContext<DestinationExecutionContext>(execEnvironment, netmgr),
        LocalizedDevice, SpatiallyEmbeddedDevice<Double> {

    private val randomGenerator = Random(randomSeed)
    private val _coordinates: Tuple by lazy {
        ArrayTupleImpl(nodePosition.latitude, nodePosition.longitude)
    }

    override fun nextRandomDouble(): Double = randomGenerator.nextDouble()
    override fun getDeviceUID(): DeviceUID = _deviceUID
    override fun getCurrentTime(): Number = LocalDateTime.now().second

    override fun instance(): DestinationExecutionContext =
        DestinationExecutionContext(
            _deviceUID,
            nodePosition,
            netmgr,
            randomSeed,
            execEnvironment
        )

    override fun getCoordinates(): Tuple = _coordinates
    override fun nbrVector(): Field<Tuple> = TODO("not implemented")
    override fun nbrRange(): Field<Double> = TODO("not implemented")
}