package it.unibo.protelis.node

import it.unibo.protelis.executionContext.UserExecutionContext
import it.unibo.protelis.model.GPSPosition
import it.unibo.protelis.model.SensorType
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram

class UserNode(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    userDeviceUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: GPSPosition,
    sensorTypes: List<SensorType> = emptyList()) : SensorNode(protelisProgram, sleepTime, userDeviceUID, applicationUID, mqttAddress, position, sensorTypes) {

    override fun createContext(): ExecutionContext =
        UserExecutionContext(this, applicationUID, mqttAddress, networkManager)
}