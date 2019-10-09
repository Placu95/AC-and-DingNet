package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.model.GPSPosition
import it.unibo.acdingnet.protelis.model.SensorType
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ProtelisProgram

abstract class NodeWithSensor(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttAddress: String,
    position: GPSPosition,
    val sensorTypes: List<SensorType>
    ) : GenericNode(protelisProgram, sleepTime, sensorDeviceUID, applicationUID, mqttAddress, position)