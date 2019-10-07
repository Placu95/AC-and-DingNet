package it.unibo.protelis.model

import it.unibo.protelis.sensor.SensorExecutionContext
import it.unibo.protelis.utils.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM

class SensorNode(
    protelisProgram: ProtelisProgram,
    val sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    val position: GPSPosition,
    val sensorTypes: List<SensorType>) {

    init {
        val networkManager = MQTTNetworkManager(sensorDeviceUID, mqttAddress, applicationUID)
        val executionContext = SensorExecutionContext(this, applicationUID, mqttAddress, networkManager)
        val protelisVM = ProtelisVM(protelisProgram, executionContext)
    }
}