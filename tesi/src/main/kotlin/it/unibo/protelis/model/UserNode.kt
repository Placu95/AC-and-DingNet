package it.unibo.protelis.model

import it.unibo.protelis.user.UserExecutionContext
import it.unibo.protelis.utils.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM

class UserNode(
    protelisProgram: ProtelisProgram,
    val userDeviceUID: StringUID,
    val position: GPSPosition,
    applicationUID: String,
    mqttAddress: String,
    val sensorTypes: List<SensorType> = emptyList()) {

    init {
        val networkManager = MQTTNetworkManager(userDeviceUID, mqttAddress, applicationUID)
        val executionContext = UserExecutionContext(this, applicationUID, mqttAddress, networkManager)
        val protelisVM = ProtelisVM(protelisProgram, executionContext)
    }
}