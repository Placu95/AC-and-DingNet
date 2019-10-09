package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.executioncontext.DestinationExecutionContext
import it.unibo.acdingnet.protelis.model.GPSPosition
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram

class DestinationNode(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    destinationUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: GPSPosition
) : GenericNode(protelisProgram, sleepTime, destinationUID, applicationUID, mqttAddress, position) {

    override fun createContext(): ExecutionContext =
        DestinationExecutionContext(deviceUID, position, networkManager)
}