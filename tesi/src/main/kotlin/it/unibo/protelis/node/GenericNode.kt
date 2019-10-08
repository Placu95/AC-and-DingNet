package it.unibo.protelis.node

import it.unibo.protelis.model.GPSPosition
import it.unibo.protelis.model.SensorType
import it.unibo.protelis.utils.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM

abstract class GenericNode(
    private val protelisProgram: ProtelisProgram,
    val deviceUID: StringUID,
    val applicationUID: String,
    val mqttAddress: String,
    val position: GPSPosition,
    val sensorTypes: List<SensorType>
) {

    val networkManager = MQTTNetworkManager(deviceUID, mqttAddress, applicationUID)
    val executionContext by lazy { createContext() }
    val protelisVM by lazy { ProtelisVM(protelisProgram, executionContext) }

    protected abstract fun createContext(): ExecutionContext
}