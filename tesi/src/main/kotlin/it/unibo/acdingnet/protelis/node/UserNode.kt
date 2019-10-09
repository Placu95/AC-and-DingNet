package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.executioncontext.UserExecutionContext
import it.unibo.acdingnet.protelis.model.GPSPosition
import it.unibo.acdingnet.protelis.model.SensorType
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
        UserExecutionContext(
            this,
            applicationUID,
            mqttAddress,
            networkManager
        )
}