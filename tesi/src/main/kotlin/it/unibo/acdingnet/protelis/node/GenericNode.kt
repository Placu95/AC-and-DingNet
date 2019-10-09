package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.networkmanager.MQTTNetworkManager
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.ProtelisVM

abstract class GenericNode(
    val protelisProgram: ProtelisProgram,
    val sleepTime: Long,
    val deviceUID: StringUID,
    val applicationUID: String,
    val mqttAddress: String,
    val position: LatLongPosition
) {

    protected val networkManager =
        MQTTNetworkManager(deviceUID, mqttAddress, applicationUID)
    protected val executionContext by lazy { createContext() }
    private val protelisVM by lazy { ProtelisVM(protelisProgram, executionContext) }

    protected abstract fun createContext(): ExecutionContext

    fun runVM() = protelisVM.runCycle()
}