package it.unibo.protelis.node

import it.unibo.protelis.executionContext.SensorExecutionContext
import it.unibo.protelis.model.GPSPosition
import it.unibo.protelis.model.SensorType
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram

class SensorNode(
    protelisProgram: ProtelisProgram,
    sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: GPSPosition,
    sensorTypes: List<SensorType>) : GenericNode(protelisProgram, sensorDeviceUID, applicationUID, mqttAddress, position, sensorTypes) {

    override fun createContext(): ExecutionContext =
        SensorExecutionContext(this, applicationUID, mqttAddress, networkManager)
}