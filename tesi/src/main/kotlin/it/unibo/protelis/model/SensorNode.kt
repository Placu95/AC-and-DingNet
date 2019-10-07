package it.unibo.protelis.model

import org.protelis.lang.datatype.impl.StringUID
import java.net.InetSocketAddress

class SensorNode(val sensorDeviceUID: StringUID, val address: InetSocketAddress, val position: GPSPosition, val sensorTypes: Set<SensorType>) {
}