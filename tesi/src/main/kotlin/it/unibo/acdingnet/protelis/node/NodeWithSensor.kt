package it.unibo.acdingnet.protelis.node

import it.unibo.acdingnet.protelis.model.LatLongPosition
import it.unibo.acdingnet.protelis.model.SensorType
import it.unibo.acdingnet.protelis.mqtt.MqttClientBasicApi
import org.protelis.lang.datatype.impl.StringUID
import org.protelis.vm.ProtelisProgram

abstract class NodeWithSensor(
    protelisProgram: ProtelisProgram,
    sleepTime: Long,
    sensorDeviceUID: StringUID,
    applicationUID: String,
    mqttClient: MqttClientBasicApi,
    position: LatLongPosition,
    val sensorTypes: List<SensorType>
    ) : GenericNode(protelisProgram, sleepTime, sensorDeviceUID, applicationUID, mqttClient, position)