package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.executioncontext.SensorExecutionContext
import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.SensorType
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram


//TODO if is only a sensor node (not an extension) check sensorTypes size > 0
open class SensorNode(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: LatLongPosition,
    sensorTypes: List<SensorType>) : NodeWithSensor(protelisProgram, sleepTime, sensorDeviceUID, applicationUID, mqttAddress, position, sensorTypes) {

    override fun createContext(): ExecutionContext =
        SensorExecutionContext(
            this,
            applicationUID,
            mqttAddress,
            networkManager
        )
}