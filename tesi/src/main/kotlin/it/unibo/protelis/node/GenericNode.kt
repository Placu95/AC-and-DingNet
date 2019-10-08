package it.unibo.protelis.node

import it.unibo.protelis.model.GPSPosition
import it.unibo.protelis.utils.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM

abstract class GenericNode(
    val protelisProgram: ProtelisProgram,
    val deviceUID: StringUID,
    val applicationUID: String,
    val mqttAddress: String,
    val position: GPSPosition
) {

    protected val networkManager = MQTTNetworkManager(deviceUID, mqttAddress, applicationUID)
    protected val executionContext by lazy { createContext() }
    protected val protelisVM by lazy { ProtelisVM(protelisProgram, executionContext) }

    protected abstract fun createContext(): ExecutionContext
}