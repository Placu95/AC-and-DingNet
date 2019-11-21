package it.unibo.acdingnet.protelis.mqtt

import it.unibo.acdingnet.protelis.model.LoRaTransmission

interface MqttMessageType

data class LoRaTransmissionWrapper(val transmission: LoRaTransmission): MqttMessageType
