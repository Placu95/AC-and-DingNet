package it.unibo.protelis.node

import it.unibo.protelis.executionContext.DestinationExecutionContext
import it.unibo.protelis.model.GPSPosition
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram

class DestinationNode(
    protelisProgram: ProtelisProgram,
    destinationUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: GPSPosition
) : GenericNode(protelisProgram, destinationUID, applicationUID, mqttAddress, position) {

    override fun createContext(): ExecutionContext = DestinationExecutionContext(deviceUID, position, networkManager)
}